import { Injectable, OnDestroy } from '@angular/core';
import { MyProfile } from './profile/profile.model';
import { Observable, BehaviorSubject, Subscription } from 'rxjs';
import { ProfileService } from './profile/profile.service';
import { NotificationService } from './notification/notification.service';

@Injectable({
    providedIn: 'root'
})
export class ProfileDataService implements OnDestroy {
    myProfile: MyProfile;
    private getProfileSubject = new BehaviorSubject<any>('');
    getProfile$ = this.getProfileSubject.asObservable();
    notificationSubscription: Subscription;
    initNotification = false;
    
    constructor(private profileService: ProfileService,
                private notificationService: NotificationService) {
    }

    setProfile(p: MyProfile) {
        this.getProfileSubject.next(p);
    }

    getProfile(): Observable<MyProfile> {
        if (!this.initNotification) {
            this.initNotification = true;
            this.subNotification();
        }
        if (!this.myProfile) {
            this.getTheProfile().then();
            return this.getProfile$;
        } else {
            return this.getProfile$;
        }
    }

    async getTheProfile(): Promise<any> {
        await this.profileService.getMyProfile().toPromise().then(res => {
            this.myProfile = res.body;
            this.setProfile(this.myProfile);
        });
    }

    subNotification() {
        this.notificationSubscription = this.notificationService.getNotification$.subscribe(res => {
            if (res.notification === 'follow') {
                this.myProfile.followersCount++;
                this.setProfile(this.myProfile);
            }
            if (res.notification === 'unfollow') {
                this.myProfile.followersCount--;
                this.setProfile(this.myProfile);
            }
        });

    }

    logout() {
        this.myProfile = undefined;
        this.setProfile(this.myProfile);
    }

    ngOnDestroy(): void {
        this.notificationSubscription.unsubscribe();
    }

}
