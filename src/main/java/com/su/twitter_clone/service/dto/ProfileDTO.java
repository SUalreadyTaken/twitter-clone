package com.su.twitter_clone.service.dto;

public class ProfileDTO {

    private int id;
    private String displayName;
    private String login;
    private String description;
    private int followingCount;
    private int followersCount;
    private int tweetsCount;

    public ProfileDTO(int id, String displayName, String login, String description, int followingCount, int followersCount, int tweetsCount) {
        this.id = id;
        this.displayName = displayName;
        this.login = login;
        this.description = description;
        this.followingCount = followingCount;
        this.followersCount = followersCount;
        this.tweetsCount = tweetsCount;
    }

    public ProfileDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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
        return "ProfileDTO{" +
            "id=" + id +
            ", displayName='" + displayName + '\'' +
            ", description='" + description + '\'' +
            ", followingCount=" + followingCount +
            ", followersCount=" + followersCount +
            ", tweetsCount=" + tweetsCount +
            '}';
    }
}
