import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';

import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { NotificationModel } from './NotificationModel.model';

@Injectable({
    providedIn: 'root'
})
export class NotificationService implements OnDestroy {

    private stompClient;
    notificationModel: NotificationModel;
    private getNewTweetSubject = new BehaviorSubject<any>('');
    gotId = false;
    getNewTweet$ = this.getNewTweetSubject.asObservable();

    constructor() {
    }

    /*
      todo
      make different subjects newTweets, deleteTweet, unFollowed/followed ( +/- followers)
     */

    initializeWebSocketConnection(id: number) {
        if (!this.gotId) {
            this.gotId = true;
            const ws = new SockJS(SERVER_API_URL + '/socket', {transports: ['websocket']});
            this.stompClient = Stomp.over(ws);
            this.stompClient.connect({}, () => {
                this.stompClient.subscribe('/notification/' + id, res => {
                    if (res.body) {
                        this.notificationModel = JSON.parse(res.body);
                        if (this.notificationModel.notification === 'new_tweet') {
                            this.setNotification(this.notificationModel);
                        }
                    }
                });
            });

        }
    }

    setNotification(n: NotificationModel) {
        this.getNewTweetSubject.next(n);
    }

    ngOnDestroy(): void {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
        }
        console.log('Disconnected');
    }
}
