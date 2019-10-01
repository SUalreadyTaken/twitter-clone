import { Component, OnInit, OnDestroy } from '@angular/core';
import { SearchService } from '../core/search/search.service';
import { NgForm } from '@angular/forms';
import { MyProfile } from '../core/profile/profile.model';
import { Tweet } from '../core/tweet/tweet.model';
import { ProfileFollow } from 'app/core/profile-follow.model';
import { ProfileService } from 'app/core/profile/profile.service';
import { ProfileDataService } from 'app/core/profile-data.service';
import { AccountService, LoginModalService } from '../core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
    selector: 'jhi-search',
    templateUrl: './search.component.html',
    styleUrls: ['../home/home.css']
})
export class SearchComponent implements OnInit, OnDestroy {

    profilesResult: ProfileFollow[];
    tweetsResult: Tweet[];
    searchTweets = true;
    empty = false;
    myProfile: MyProfile;
    modalRef: NgbModalRef;
    loggedIn = false;
    eventSubscription: Subscription;

    constructor(private searchService: SearchService,
                private profileService: ProfileService,
                private profileDataService: ProfileDataService,
                private accountService: AccountService,
                private eventManager: JhiEventManager,
                private loginModalService: LoginModalService) {
    }

    ngOnInit() {
        this.accountService.identity().then((account: Account) => {
            if (account === null) {
                this.login();
            } else {
                this.init()
            }
        });

        this.eventSubscription = this.eventManager.subscribe('authenticationSuccess', () => {
            this.init();
        });
    }

    init() {
        this.profileDataService.getProfile().subscribe(res => {
            if (res) {
                this.myProfile = res;
                if (!this.loggedIn) {
                    this.loggedIn = true;
                }
            }
        })
    }

    onSearch(form: NgForm) {
        if (this.searchTweets) {
            // search tweets
            this.searchService.searchTweets(form.value.search).subscribe(res => {
                this.tweetsResult = res.body;
                this.empty = res.body.length === 0;
            });
        } else {
            // search profiles
            this.searchService.searchProfiles(form.value.search).subscribe(res => {
                this.profilesResult = res.body;
                this.empty = res.body.length === 0;
            });
        }
    }

    // maybe reset the array as well ?

    onTweet(form: NgForm) {
        this.searchTweets = true;
        form.resetForm();
    }

    onProfile(form: NgForm) {
        this.searchTweets = false;
        form.resetForm();
    }

    onSetFollow(id: number) {
        this.profileService.setFollow(id).subscribe();
        const targetProfile = this.profilesResult.find(profile => profile.id === id);
        targetProfile.following = true;
        targetProfile.followersCount++;
        this.myProfile.followingCount++;
        this.profileDataService.setProfile(this.myProfile);
    }

    onDeleteFollow(id: number) {
        this.profileService.deleteFollow(id).subscribe();
        const targetProfile = this.profilesResult.find(profile => profile.id === id);
        targetProfile.following = false;
        targetProfile.followersCount--;
        this.myProfile.followingCount--;
        this.profileDataService.setProfile(this.myProfile);
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    ngOnDestroy(): void {
        this.eventSubscription.unsubscribe();
    }

}
