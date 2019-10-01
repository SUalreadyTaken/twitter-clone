import { Component, OnInit, OnDestroy } from '@angular/core';
import { ProfileService } from 'app/core/profile/profile.service';
import { MyProfile } from 'app/core/profile/profile.model';
import { ProfileDataService } from 'app/core/profile-data.service';
import { AccountService, LoginModalService } from '../../core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
    selector: 'jhi-profile',
    templateUrl: './my-profile.component.html',
    styleUrls: ['../home.css']
})
export class MyProfileComponent implements OnInit, OnDestroy {
    profile: MyProfile;
    gotProfile = false;
    modalRef: NgbModalRef;
    profileSubscription: Subscription;
    eventSubscription: Subscription;

    constructor(private profileService: ProfileService,
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
                this.getProfile();
            }
        });

        this.eventSubscription = this.eventManager.subscribe('authenticationSuccess', () => {
            this.getProfile();
        });
    }

    getProfile() {
        this.profileSubscription = this.profileDataService.getProfile().subscribe(p => {
            if (p) {
               this.profile = p;
               this.gotProfile = true;
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
