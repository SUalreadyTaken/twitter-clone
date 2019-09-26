import { Component, OnInit, OnDestroy } from '@angular/core';
import { TweetService } from 'app/core/tweet/tweet.service';
import { Tweet } from 'app/core/tweet/tweet.model';
import { AccountService } from 'app/core';
import { MyProfile } from 'app/core/profile/profile.model';
import { ProfileDataService } from 'app/core/profile-data.service';
import { NgForm } from '@angular/forms';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { LoginModalService } from 'app/core';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription } from 'rxjs/internal/Subscription';
import { ProfileService } from 'app/core/profile/profile.service';
import { NotificationService } from 'app/core/notification/notification.service';

@Component({
    selector: 'jhi-timeline',
    templateUrl: './timeline.component.html',
    styleUrls: ['../home.css']
})
export class TimelineComponent implements OnInit, OnDestroy {
    isLoggedIn = false;
    timeline: Tweet[];
    profile: MyProfile;
    lastTweetId: number;
    modalRef: NgbModalRef;
    profileSubscription: Subscription;
    eventSubscription: Subscription;
    newTweetSubscription: Subscription;
    ntSubInit = false;
    isMore = true;
    // need it for length
    postTweetContent = '';
    newTweet = false;

    constructor(private tweetService: TweetService,
                private accountService: AccountService,
                private profileDataService: ProfileDataService,
                private loginModalService: LoginModalService,
                private profileService: ProfileService,
                private eventManager: JhiEventManager,
                private notificationService: NotificationService) {
    }

    // todo if empty say so

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

    init() {
        this.getTimeline();
        this.profileSubscription = this.profileDataService.getProfile().subscribe(p => {
                if (p) {
                    if (!this.profile) {
                        this.profile = p;
                        this.notificationService.initializeWebSocketConnection(this.profile.id);
                    }
                }
            }
        );

        this.newTweetSubscription = this.notificationService.getNewTweet$.subscribe(res => {
            if (this.ntSubInit && res.notification === 'new_tweet') {
                this.newTweet = true;
            }
            if (!this.ntSubInit) {
                this.ntSubInit = true;
            }
        });

    }

    getTimeline() {
        this.tweetService.getTimeline().then(res => {
            this.timeline = res;
            if (this.timeline.length > 0) {
                this.lastTweetId = this.timeline[res.length - 1].id;
            } else {
                this.isMore = false;
            }
            this.isLoggedIn = true;
        });
    }

    onNewTweet() {
        this.newTweet = false;
        this.getTimeline();
    }

    onPostTweet(form: NgForm) {
        this.tweetService.postTweet(form.value.content).subscribe(res => {
            this.timeline.unshift(res.body);
            this.profile.tweetsCount++;
            this.profileDataService.setProfile(this.profile);
            this.lastTweetId = res.body.id;
            this.tweetService.setTimeline(this.timeline);
        });
        form.reset();
        this.postTweetContent = '';
    }

    deleteTweet(id: number, tweet: Tweet) {
        const index: number = this.timeline.indexOf(tweet);
        if (index !== -1) {
            this.timeline.splice(index, 1);
            this.tweetService.deleteTweet(id);
            this.profile.tweetsCount--;
            this.profileDataService.setProfile(this.profile);
            this.tweetService.setTimeline(this.timeline);
        }
    }

    onMoreTweets() {
        this.tweetService.getMoreTimelineTweets(this.lastTweetId, this.timeline.length).subscribe(res => {
            if (res.body.length > 0) {
                this.timeline.push(...res.body);
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
        this.newTweetSubscription.unsubscribe();
    }

}
