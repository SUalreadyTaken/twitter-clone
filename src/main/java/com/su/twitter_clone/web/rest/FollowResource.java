package com.su.twitter_clone.web.rest;

import com.su.twitter_clone.domain.Profile;
import com.su.twitter_clone.domain.User;
import com.su.twitter_clone.repository.ProfileRepository;
import com.su.twitter_clone.service.FollowService;
import com.su.twitter_clone.service.UserService;
import com.su.twitter_clone.service.dto.ProfileFollowDTO;
import com.su.twitter_clone.web.rest.errors.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class FollowResource {
    private final Logger log = LoggerFactory.getLogger(FollowResource.class);
    private final FollowService followService;
    private final UserService userService;
    private final ProfileRepository profileRepository;

    public FollowResource(FollowService followService, UserService userService, ProfileRepository profileRepository) {
        this.followService = followService;
        this.userService = userService;
        this.profileRepository = profileRepository;
    }

    /**
     * Get targeted profiles followings
     * @param id Targeted profiles id
     * @return Following Profiles list
     */
    @GetMapping("/following")
    public List<ProfileFollowDTO> getFollowings(@RequestParam(value = "id") int id) {
        log.info("getFollowings with id " + id + " was called");
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        Optional<Profile> profile = profileRepository.findFirstByUser(user);
        return profile.isPresent() ? followService.getFollowings(profile.get(), id) : Collections.emptyList();
    }

    /**
     * Get targeted profiles followers
     * @param id Targeted profiles id
     * @return Followers Profiles list
     */
    @GetMapping("/followers")
    public List<ProfileFollowDTO> getFollowers(@RequestParam(value = "id") int id) {
        log.info("getFollowers with id " + id + " was called");
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        Optional<Profile> profile = profileRepository.findFirstByUser(user);
        return profile.isPresent() ? followService.getFollowers(profile.get(), id) : Collections.emptyList();
    }

    /**
     * Set logged in user to follow targeted profile
     * @param id Targeted profiles id
     */
    // FIXME: should return code
    @PostMapping("/follow")
    public void setFollow(@RequestBody int id) {
        log.info("setFollow with id " + id + " was called");
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        Optional<Profile> profile = profileRepository.findFirstByUser(user);
        profile.ifPresent(p -> followService.setFollow(p, id));

    }

    /**
     * Delete logged in users following of targeted profile
     * @param id Targeted profiles id
     */
    // FIXME: should return code
    @DeleteMapping("/follow")
    public void deleteFollow(@RequestParam(value="id") int id) {
        log.info("deleteFollow with id " + id + " was called");
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        Optional<Profile> profile = profileRepository.findFirstByUser(user);
        profile.ifPresent(p -> followService.deleteFollow(p, id));

    }



}
