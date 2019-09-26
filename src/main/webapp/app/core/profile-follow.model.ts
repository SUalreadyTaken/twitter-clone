export class ProfileFollow {
    public id: number;
    public displayName: string;
    public login: string;
    public description: string;
    public followingCount: number;
    public followersCount: number;
    public tweetsCount: number;
    public following: boolean;

    constructor(id: number, displayName: string, login: string, 
                description: string, followingCount: number, 
                followersCount: number, tweetsCount: number, following: boolean) {
    }
}
