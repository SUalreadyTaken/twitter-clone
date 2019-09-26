package com.su.twitter_clone.web.rest;

import com.su.twitter_clone.domain.User;
import com.su.twitter_clone.service.SearchService;
import com.su.twitter_clone.service.UserService;
import com.su.twitter_clone.service.dto.ProfileFollowDTO;
import com.su.twitter_clone.service.dto.TweetDTO;
import com.su.twitter_clone.web.rest.errors.InternalServerErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchResource {

    private final Logger log = LoggerFactory.getLogger(SearchResource.class);
    private final UserService userService;
    private final SearchService searchService;

    public SearchResource(UserService userService, SearchService searchService) {
        this.userService = userService;
        this.searchService = searchService;
    }

    /**
     * Get list of tweets containing tweetContent
     * @param tweetContent Search string
     * @return List of TweetDTOs containing searching tweetContent
     */
    @GetMapping("/search/tweets")
    public List<TweetDTO> searchTweets(@RequestParam(value = "search") String tweetContent) {
        log.info("tweetContent >> " + tweetContent);
        userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        return !StringUtils.isBlank(tweetContent) ? searchService.searchTweets(tweetContent) : Collections.emptyList();
    }

    /**
     * Get list of profiles whose login or displayName contains searching string
     * @param searching Search string
     * @return List of ProfileFollowDTOs
     */
    @GetMapping("/search/profiles")
    public List<ProfileFollowDTO> searchProfiles(@RequestParam(value = "search") String searching) {
        log.info("searching for >> " + searching);
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        return !StringUtils.isBlank(searching) ? searchService.searchProfiles(searching, user) : Collections.emptyList();
    }


}
