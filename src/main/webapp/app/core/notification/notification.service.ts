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
    getNotification$ = this.getNewTweetSubject.asObservable();

    constructor() {
    }

    initializeWebSocketConnection(id: number) {
        if (!this.gotId) {
            this.gotId = true;
            const ws = new SockJS(SERVER_API_URL + '/socket', {transports: ['websocket']});
            this.stompClient = Stomp.over(ws);
            this.stompClient.connect({}, () => {
                this.stompClient.subscribe('/notification/' + id, res => {
                    if (res.body) {
                        console.log('got this message > ' + res.body);
                        if (!this.notificationModel) {
                            this.notificationModel = new NotificationModel(res.body.toString());
                        } else {
                            this.notificationModel.notification = res.body.toString();
                        }
                        this.setNotification(this.notificationModel);
                    }
                });
            });

        }
    }

    setNotification(n: NotificationModel) {
        this.getNewTweetSubject.next(n);
    }

    logout() {
        this.disconnect();
        this.gotId = false;
    }

    ngOnDestroy(): void {
        this.disconnect();
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
        }
    }

}
