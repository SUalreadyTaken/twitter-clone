import { Injectable } from '@angular/core';

import { AccountService } from 'app/core/auth/account.service';
import { AuthServerProvider } from 'app/core/auth/auth-jwt.service';
import { TweetService } from '../tweet/tweet.service';
import { ProfileDataService } from '../profile-data.service';
import { NotificationService } from '../notification/notification.service';

@Injectable({ providedIn: 'root' })
export class LoginService {
    constructor(private accountService: AccountService,
                private authServerProvider: AuthServerProvider,
                private tweetService: TweetService,
                private profileDataService: ProfileDataService,
                private notificationService: NotificationService) {}

    login(credentials, callback?) {
        const cb = callback || function() {};

        return new Promise((resolve, reject) => {
            this.authServerProvider.login(credentials).subscribe(
                data => {
                    this.accountService.identity(true).then(account => {
                        resolve(data);
                    });
                    return cb();
                },
                err => {
                    this.logout();
                    reject(err);
                    return cb(err);
                }
            );
        });
    }

    loginWithToken(jwt, rememberMe) {
        return this.authServerProvider.loginWithToken(jwt, rememberMe);
    }

    logout() {
        this.authServerProvider.logout().subscribe();
        this.accountService.authenticate(null);
        this.tweetService.logout();
        this.profileDataService.logout();
        this.notificationService.logout();
    }
}
