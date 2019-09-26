package com.su.twitter_clone.web.rest;

import com.su.twitter_clone.service.MockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock")
public class CreateMock {

    private final MockService mockService;

    public CreateMock(MockService mockService) {
        this.mockService = mockService;
    }

    /**
     * Creates new users and profiles
     * @param amount Amount to create
     */
    @GetMapping("/users/{amount}")
    public void createUsers(@PathVariable int amount) {
        mockService.createUser(amount);
    }

    /**
     * Creates new followings. Takes random number between 0 - amount for each existing user
     * @param amount Max amount of followings per user
     */
    @GetMapping("/following/{amount}")
    public void createFollowing(@PathVariable int amount) {
        mockService.createFollowing(amount);
    }

    /**
     * Creates new tweets
     * @param amount Amount of tweets to be created
     */
    @GetMapping("/tweets/{amount}")
    public void createTweets(@PathVariable int amount) {
        mockService.createTweets(amount);
    }

}
