import { Injectable, OnDestroy } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from 'app/app.constants';
import { Tweet } from 'app/core/tweet/tweet.model';
import { Observable, Subscription } from 'rxjs';
import { NotificationService } from '../notification/notification.service';

@Injectable()
export class TweetService implements OnDestroy {
    timeline: Tweet[];
    newTweetSubscription: Subscription;
    gotNewTweet = true;

    constructor(private http: HttpClient, private notificationService: NotificationService) {
    }

    initSub() {
        this.newTweetSubscription = this.notificationService.getNotification$.subscribe(r => {
            if (r.notification === 'new_tweet') {
                this.gotNewTweet = true;
            }
        });
    }

    async getTimeline(): Promise<Tweet[]> {
        if (!this.newTweetSubscription) {
            this.initSub();
        }
        if (!this.timeline || this.gotNewTweet) {
            await this.http
                .get<Tweet[]>(SERVER_API_URL + 'api/tweets/timeline', {observe: 'response'})
                .toPromise()
                .then(res => {
                    this.timeline = res.body;
                    this.gotNewTweet = false;
                });
            return new Promise<Tweet[]>(resolve => {
                resolve(this.timeline);
            });
        } else {
            return new Promise<Tweet[]>(resolve => {
                resolve(this.timeline);
            });
        }
    }

    setTimeline(t: Tweet[]) {
        this.timeline = t;
    }

    removeFromTimeline(t: Tweet) {
        if (this.timeline) {
            const tweet = this.timeline.find(x => x.content === t.content);
            const index: number = this.timeline.indexOf(tweet);
            if (index !== -1) {
                this.timeline.splice(index, 1);
            }
        }
    }

    changeTimeline() {
        this.gotNewTweet = true;
    }

    logout() {
        this.timeline = undefined;
        this.gotNewTweet = false;
    }

    getProfileTweets(profileId: number): Observable<HttpResponse<Tweet[]>> {
        return this.http.get<Tweet[]>(SERVER_API_URL + 'api/tweets/profiles/' + profileId, {observe: 'response'});
    }

    getMoreProfileTweets(profileId: number, lastTweetId: number, alreadyReceived: number): Observable<HttpResponse<Tweet[]>> {
        return this.http.get<Tweet[]>(SERVER_API_URL + 'api/tweets/profile/more', {
            observe: 'response',
            params: {
                id: profileId.toString(),
                lastTweetId: lastTweetId.toString(),
                already: alreadyReceived.toString()
            }
        });
    }

    getMoreTimelineTweets(tweetId: number, alreadyReceived: number): Observable<HttpResponse<Tweet[]>> {
        return this.http.get<Tweet[]>(SERVER_API_URL + 'api/tweets/more', {
            observe: 'response',
            params: {lastTweetId: tweetId.toString(), already: alreadyReceived.toString()}
        });
    }

    postTweet(content: string): Observable<HttpResponse<Tweet>> {
        return this.http.post<Tweet>(SERVER_API_URL + 'api/tweets/tweet', content, {observe: 'response'});
    }

    deleteTweet(id: number) {
        this.http
            .delete<any>(SERVER_API_URL + 'api/tweets/delete', {
                observe: 'response',
                params: {id: id.toString()}
            })
            .subscribe(res => {
            });
    }

    ngOnDestroy(): void {
        this.newTweetSubscription.unsubscribe();
    }
}
