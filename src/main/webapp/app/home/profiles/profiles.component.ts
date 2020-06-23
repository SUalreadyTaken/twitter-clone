import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { TweetService } from 'app/core/tweet/tweet.service';
import { Tweet } from 'app/core/tweet/tweet.model';
import { AccountService, LoginModalService } from 'app/core';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { ProfileService } from 'app/core/profile/profile.service';
import { ProfileFollow } from 'app/core/profile-follow.model';
import { ProfileDataService } from 'app/core/profile-data.service';
import { MyProfile } from 'app/core/profile/profile.model';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
    selector: 'jhi-profile-timeline',
    templateUrl: './profiles.component.html',
    styleUrls: ['../home.css']
})
export class ProfilesComponent implements OnInit, OnDestroy {
    modalRef: NgbModalRef;
    id: number;
    tweetsList: Tweet[];
    isLoggedIn = false;
    profile: ProfileFollow;
    myProfile: MyProfile;
    // 0 - me | 1 - following | 2 not following
    buttonType = 0;
    profileSubscription: Subscription;
    eventSubscription: Subscription;
    isMore: boolean;
    lastTweetId: number;

    constructor(private route: ActivatedRoute,
                private tweetService: TweetService,
                private accountService: AccountService,
                private eventManager: JhiEventManager,
                private loginModalService: LoginModalService,
                private profileDataService: ProfileDataService,
                private profileService: ProfileService) {

    }

    ngOnInit() {
        this.accountService.identity().then((account: Account) => {
            if (account === null) {
                this.login();
            } else {
                this.init();
            }
        });
        this.eventSubscription = this.eventManager.subscribe('authenticationSuccess', () => {
            this.init();
        });

    }

    getTweetsList() {
        this.tweetService.getProfileTweets(this.id).subscribe(res => {
            // todo if timeline is empty list then say so
            this.tweetsList = res.body;
            if (this.tweetsList.length > 0) {
                this.lastTweetId = this.tweetsList[res.body.length - 1].id;
            } else {
                this.isMore = false;
            }
        });
    }

    init() {
        this.route.params.subscribe(
            (params: Params) => {
                this.id = +params['id'];
                this.isMore = true;
                this.profileSubscription = this.profileDataService.getProfile().subscribe(p => {
                    if (p) {
                        this.myProfile = p;
                        this.getProfile();
                    }
                });
                this.getTweetsList();
            }
        );
    }

    getProfile() {
            this.profileService.getProfileById(this.id).subscribe(profileById => {
                this.profile = profileById.body;
                if (this.myProfile.id !== this.profile.id) {
                    this.profileService.ifFollowing(this.id).subscribe(boolean => {
                        this.isLoggedIn = true;
                        if (boolean.body.valueOf()) {
                            this.buttonType = 1;
                        } else {
                            this.buttonType = 2;
                        }
                    });
                } else {
                    this.buttonType = 0;
                    this.isLoggedIn = true;
                }
            });
    }

    onSetFollow(id: number) {
        this.profileService.setFollow(id).subscribe();
        this.buttonType = 1;
        this.myProfile.followingCount++;
        this.profileDataService.setProfile(this.myProfile);
    }

    onDeleteFollow(id: number) {
        this.profileService.deleteFollow(id).subscribe();
        this.buttonType = 2;
        this.myProfile.followingCount--;
        this.profileDataService.setProfile(this.myProfile);
    }

    deleteTweet(id: number, tweet: Tweet) {
        const index: number = this.tweetsList.indexOf(tweet);
        if (index !== -1) {
            this.tweetsList.splice(index, 1);
            this.tweetService.deleteTweet(id);
            this.profile.tweetsCount--;
            this.profileDataService.setProfile(this.profile);
            this.tweetService.removeFromTimeline(tweet);
        }
    }

    onEdit() {
        console.log('NEED TO IMPLEMENT EDIT');
    }

    onMoreTweets() {
        this.tweetService.getMoreProfileTweets(this.id, this.lastTweetId, this.tweetsList.length).subscribe(res => {
            if (res.body.length > 0) {
                this.tweetsList.push(...res.body);
                this.lastTweetId = res.body[res.body.length - 1].id;
            } else {
                this.isMore = false;
            }
        });
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    ngOnDestroy(): void {
        this.profileSubscription.unsubscribe();
        this.eventSubscription.unsubscribe();
    }

}
