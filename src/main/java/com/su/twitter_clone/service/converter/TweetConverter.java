package com.su.twitter_clone.service.converter;

import com.su.twitter_clone.domain.Tweet;
import com.su.twitter_clone.service.dto.TweetDTO;

public class TweetConverter {

    public static TweetDTO getTweetDTO(Tweet tweet) {
        return new TweetDTO(tweet.getId(), tweet.getProfile().getId(), tweet.getProfile().getDisplayName(),
                     tweet.getProfile().getUser().getLogin(), tweet.getContent(), tweet.getTime());
    }
}
