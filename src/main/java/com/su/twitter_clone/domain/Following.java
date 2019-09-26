package com.su.twitter_clone.domain;

import javax.persistence.*;

@Entity
@Table(name = "following")
public class Following {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private Profile followingUser;

    public Following() {}

    public Following(Profile profile, Profile followingUser) {
        this.profile = profile;
        this.followingUser = followingUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile user) {
        this.profile = user;
    }

    public Profile getFollowingUser() {
        return followingUser;
    }

    public void setFollowingUser(Profile followingUser) {
        this.followingUser = followingUser;
    }


}
