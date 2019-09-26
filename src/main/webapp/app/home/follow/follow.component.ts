import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { ProfileService } from 'app/core/profile/profile.service';
import { ProfileDataService } from 'app/core/profile-data.service';
import { Account, AccountService, LoginModalService } from '../../core';
import { JhiEventManager } from 'ng-jhipster';
import { ProfileFollow } from 'app/core/profile-follow.model';
import { MyProfile } from 'app/core/profile/profile.model';
import { Subscription } from 'rxjs/internal/Subscription';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-follow',
    templateUrl: './follow.component.html',
    styleUrls: ['../home.css']
})
export class FollowComponent implements OnInit, OnDestroy {

    profileFollowList: ProfileFollow[];
    empty = false;
    myProfile: MyProfile;
    modalRef: NgbModalRef;
    isLoggedIn = false;
    profileSubscription: Subscription;
    eventSubscription: Subscription;
    // for empty list right message
    followers = true;

    constructor(private route: ActivatedRoute,
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
                this.getProfiles();
            }
        });

        this.eventSubscription = this.eventManager.subscribe('authenticationSuccess', () => {
            this.getProfiles();
        });

    }

    getProfiles() {
        const pathString = this.route.snapshot.url[2].toString();
        if (pathString === 'followers') {
            this.getFollowers();
            this.followers = true;
        } else {
            this.getFollowings();
            this.followers = false;
        }
    }

    getFollowers() {
        this.route.params.subscribe((params: Params) => {
            const id = +params['id'];
            this.profileService.getFollowerProfiles(id).subscribe(res => {
                this.profileFollowList = res.body;
                this.empty = res.body.length === 0;
                // this.isLoggedIn = true;
                this.profileSubscription = this.profileDataService.getProfile().subscribe(p => {
                    this.myProfile = p;
                    if (this.myProfile.id) {
                        this.isLoggedIn = true;
                    }
                });
            });
        });
    }

    getFollowings() {
        this.route.params.subscribe((params: Params) => {
            const id = +params['id'];
            this.profileService.getFollowingProfiles(id).subscribe(res => {
                this.profileFollowList = res.body;
                this.empty = res.body.length === 0;
                this.profileSubscription = this.profileDataService.getProfile().subscribe(p => {
                    this.myProfile = p;
                    if (this.myProfile.id) {
                        this.isLoggedIn = true;
                    }
                });
            });
        });
    }

    onSetFollow(id: number) {
        this.profileService.setFollow(id).subscribe();
        const targetProfile = this.profileFollowList.find(profile => profile.id === id);
        targetProfile.following = true;
        targetProfile.followersCount++;
        this.myProfile.followingCount++;
        this.profileDataService.setProfile(this.myProfile);
    }

    // todo ask conformation.. can delete multiple by accident.. too fast

    onDeleteFollow(id: number) {
        this.profileService.deleteFollow(id).subscribe();
        const targetProfile = this.profileFollowList.find(profile => profile.id === id);
        targetProfile.following = false;
        targetProfile.followersCount--;
        this.myProfile.followingCount--;
        this.profileDataService.setProfile(this.myProfile);
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    ngOnDestroy(): void {
        this.profileSubscription.unsubscribe();
        this.eventSubscription.unsubscribe();
    }
}
