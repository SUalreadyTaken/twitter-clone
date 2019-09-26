import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from 'app/app.constants';
import { Observable } from 'rxjs';
import { Tweet } from '../tweet/tweet.model';
import { ProfileFollow } from 'app/core/profile-follow.model';

@Injectable({ providedIn: 'root' })
export class SearchService {

    constructor(private http: HttpClient) {
    }

    searchProfiles(profileName: string): Observable<HttpResponse<ProfileFollow[]>> {
        return this.http.get<ProfileFollow[]>(SERVER_API_URL + 'api/search/profiles', {observe: 'response', params: {'search': profileName}});
    }

    searchTweets(tweetContent: string): Observable<HttpResponse<Tweet[]>> {
        return this.http.get<Tweet[]>(SERVER_API_URL + 'api/search/tweets', {observe: 'response', params: {'search': tweetContent}});
    }
}
