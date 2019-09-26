import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import { VERSION } from 'app/app.constants';
import { AccountService, LoginModalService, LoginService, Account } from 'app/core';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { TweetService } from 'app/core/tweet/tweet.service';
import { Subscription } from 'rxjs/internal/Subscription';
import { ProfileDataService } from '../../core/profile-data.service';
import { MyProfile } from '../../core/profile/profile.model';

@Component({
    selector: 'jhi-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['navbar.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
    inProduction: boolean;
    isNavbarCollapsed: boolean;
    languages: any[];
    swaggerEnabled: boolean;
    modalRef: NgbModalRef;
    version: string;
    account: Account;
    profileSubscription: Subscription;
    myProfile: MyProfile;

    constructor(
        private loginService: LoginService,
        private accountService: AccountService,
        private loginModalService: LoginModalService,
        private profileService: ProfileService,
        private profileDataService: ProfileDataService,
        private router: Router
    ) {
        this.version = VERSION ? 'v' + VERSION : '';
        this.isNavbarCollapsed = true;
    }

    ngOnInit() {
        this.profileService.getProfileInfo().then(profileInfo => {
            this.inProduction = profileInfo.inProduction;
            this.swaggerEnabled = profileInfo.swaggerEnabled;
        });
        this.accountService.fetch().subscribe(res => {
            this.account = res.body;
        });
        this.profileSubscription = this.profileDataService.getProfile$.subscribe(p => {
            this.myProfile = p;
        });
    }

    collapseNavbar() {
        this.isNavbarCollapsed = true;
    }

    isAuthenticated() {
        return this.accountService.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    logout() {
        this.collapseNavbar();
        this.loginService.logout();
        this.router.navigate(['']);
        // todo logout reset everything
    }

    toggleNavbar() {
        this.isNavbarCollapsed = !this.isNavbarCollapsed;
    }

    getImageUrl() {
        return this.isAuthenticated() ? this.accountService.getImageUrl() : null;
    }

    onTweets() {
        this.router.navigate(['profiles', this.myProfile.id]);
    }

    ngOnDestroy(): void {
    }
}
