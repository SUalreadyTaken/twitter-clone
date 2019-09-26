import { Injectable } from '@angular/core';
import { MyProfile } from './profile/profile.model';
import { Observable, BehaviorSubject } from 'rxjs';
import { ProfileService } from './profile/profile.service';

@Injectable({
    providedIn: 'root'
})
export class ProfileDataService {
    myProfile: MyProfile;
    private getProfileSubject = new BehaviorSubject<any>('');
    getProfile$ = this.getProfileSubject.asObservable();

    // todo sub to follow/unfollow notification.. change profile.. insert as next

    // todo logout

    constructor(private profileService: ProfileService) {
    }

    setProfile(p: MyProfile) {
        this.getProfileSubject.next(p);
    }

    getProfile(): Observable<MyProfile> {
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

}
