import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from 'app/app.constants';
import { MyProfile } from 'app/core/profile/profile.model';
import { ProfileFollow } from '../profile-follow.model';

@Injectable()
export class ProfileService {

    constructor(private http: HttpClient) {
    }

    getMyProfile(): Observable<HttpResponse<MyProfile>> {
        return this.http.get<MyProfile>(SERVER_API_URL + 'api/profiles/me', {observe: 'response'});
    }

    getFollowingProfiles(id: number): Observable<HttpResponse<ProfileFollow[]>> {
        return this.http.get<ProfileFollow[]>(SERVER_API_URL + 'api/following', {
            observe: 'response',
            params: {'id': id.toString()}
        });
    }

    getFollowerProfiles(id: number): Observable<HttpResponse<ProfileFollow[]>> {
        return this.http.get<ProfileFollow[]>(SERVER_API_URL + 'api/followers', {
            observe: 'response',
            params: {'id': id.toString()}
        });
    }

    getProfileById(id: number): Observable<HttpResponse<ProfileFollow>> {
        return this.http.get<ProfileFollow>(SERVER_API_URL + 'api/profiles', {
            observe: 'response',
            params: {'id': id.toString()}
        });
    }
    
    ifFollowing(id: number): Observable<HttpResponse<boolean>> {
        return this.http.get<boolean>(SERVER_API_URL + 'api/profiles/following', {
            observe: 'response',
            params: {'id': id.toString()}
        });
    }

    setFollow(id: number): Observable<HttpResponse<any>> {
        return this.http.post<any>(SERVER_API_URL + 'api/follow', id, {observe: 'response'});
    }

    deleteFollow(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(SERVER_API_URL + 'api/follow', {
            observe: 'response',
            params: {'id': id.toString()}
        });
    }

}
