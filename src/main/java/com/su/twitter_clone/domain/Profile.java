package com.su.twitter_clone.domain;


import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "profile")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "display_name")
    private String displayName;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Column(name = "following_count")
    private int followingCount;
    @Column(name = "followers_count")
    private int followersCount;
    @Column(name = "tweets_count")
    private int tweetsCount;

    public Profile() {}

    public Profile(User user, String displayName, String description, int followingCount, int followersCount, int tweetsCount) {
        this.user = user;
        this.displayName = displayName;
        this.description = description;
        this.followingCount = followingCount;
        this.followersCount = followersCount;
        this.tweetsCount = tweetsCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getTweetsCount() {
        return tweetsCount;
    }

    public void setTweetsCount(int tweetsCount) {
        this.tweetsCount = tweetsCount;
    }

    @Override
    public String toString() {
        return "MyProfile{" +
            "id=" + id +
            ", displayName='" + displayName + '\'' +
            ", description='" + description + '\'' +
            ", followingCount=" + followingCount +
            ", followersCount=" + followersCount +
            ", tweetsCount=" + tweetsCount +
            '}';
    }
}
