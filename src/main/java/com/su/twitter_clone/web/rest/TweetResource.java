package com.su.twitter_clone.web.rest;

import com.su.twitter_clone.domain.User;
import com.su.twitter_clone.service.TweetService;
import com.su.twitter_clone.service.UserService;
import com.su.twitter_clone.service.dto.TweetDTO;
import com.su.twitter_clone.web.rest.errors.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TweetResource {

    private final Logger log = LoggerFactory.getLogger(TweetResource.class);

    private final UserService userService;
    private final TweetService tweetService;

    public TweetResource(UserService userService, TweetService tweetService) {
        this.userService = userService;
        this.tweetService = tweetService;
    }

    /**
     * Get logged in users timeline / list of TweetDTO
     * @return List of TweetDTO
     */
    @GetMapping("/tweets/timeline")
    public List<TweetDTO> getTimeline() {
        log.info("TweetResource.getTimeline was called");
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        return tweetService.getTimelineTweets(user);
    }

    /**
     * Get next patch of logged in users timeline tweets
     * @param lastTweetId Last tweets id
     * @param alreadyReceived Amount of already received tweets
     * @return List of TweetDTO
     */
    @GetMapping("tweets/more")
    public List<TweetDTO> getMoreTimelineTweets(@RequestParam("lastTweetId") int lastTweetId, @RequestParam("already") int alreadyReceived) {
        log.info("TweetResource.getMoreTweets was called");
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        return tweetService.getMoreTimelineTweets(user, lastTweetId, alreadyReceived);
    }

    /**
     * Get targeted profiles tweets
     * @param id profile id
     * @return List of tweets. Can be empty if profile not found or no tweets by this profile
     */
    @GetMapping("/tweets/profiles/{id}")
    public List<TweetDTO> getProfileTweets(@PathVariable int id) {
        log.info("TweetResource.getUserTimeline with id " + id + " was called");
        return tweetService.getProfileTweets(id);
    }

    /**
     * Get next patch of targeted users tweets
     * @param id Targeted profiles id
     * @param lastTweetId Its last tweet id
     * @param alreadyReceived Amount of already received tweets
     * @return List of TweetDTO
     */
    @GetMapping("/tweets/profile/more")
    public List<TweetDTO> getMoreProfilesTweets(@RequestParam("id") int id, @RequestParam("lastTweetId") int lastTweetId, @RequestParam("already") int alreadyReceived) {
        log.info("TweetResource.getMoreUserTweets was called");
//        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        return tweetService.getMoreProfileTweets(id, lastTweetId, alreadyReceived);
    }

    /**
     * Create tweet
     * @param content Tweets content
     * @return TweetDTO with current time
     */
    @PostMapping("/tweets/tweet")
    public TweetDTO postTweet(@RequestBody String content) {
        log.info("TweetResource.postTweet was called");
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        return tweetService.postTweet(user, content);
    }

    /**
     * Delete tweet
     * @param id Tweets id
     * @return Status 200
     */
    @DeleteMapping("/tweets/delete")
    public ResponseEntity<Void> deleteTweet(@RequestParam("id") int id) {
        log.info("TweetResources.deleteTweet");
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        tweetService.deleteTweet(id, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
