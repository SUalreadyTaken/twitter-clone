package com.su.twitter_clone.service.dto;

public class ProfileFollowDTO extends ProfileDTO {

    private boolean following;

    public ProfileFollowDTO(int id, String displayName, String login, String description, int followingCount, int followersCount, int tweetsCount, boolean following) {
        super(id, displayName, login, description, followingCount, followersCount, tweetsCount);
        this.following = following;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }
}
