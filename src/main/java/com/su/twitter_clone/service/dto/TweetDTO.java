package com.su.twitter_clone.service.dto;

import com.su.twitter_clone.config.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class TweetDTO {

    private int id;
    private int profileId;
    private String displayName;
    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;
    private String content;
    private LocalDateTime time;

    //todo
//    private int retweets;
//    private int likes;
//    private int chatCount;

    public TweetDTO() {}

    public TweetDTO(int id, int profileId, String displayName, String login, String content, LocalDateTime time) {
        this.id = id;
        this.profileId = profileId;
        this.displayName = displayName;
        this.login = login;
        this.content = content;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TweetDTO{" +
            "id=" + id +
            ", profileId=" + profileId +
            ", displayName='" + displayName + '\'' +
            ", login='" + login + '\'' +
            ", message='" + content + '\'' +
            ", time=" + time +
            '}';
    }
}
