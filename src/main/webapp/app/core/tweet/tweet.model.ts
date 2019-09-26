export class Tweet {
    public id: number;
    public profileId: number;
    public displayName: string;
    public login: string;
    public content: string;
    public time: Date;

    constructor(id: number, profileId: number, displayName: string, login: string, content: string, time: Date) {
    }
}
