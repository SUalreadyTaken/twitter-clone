package com.su.twitter_clone.web.rest;

import com.su.twitter_clone.domain.User;
import com.su.twitter_clone.service.ProfileService;
import com.su.twitter_clone.service.UserService;
import com.su.twitter_clone.service.dto.ProfileDTO;
import com.su.twitter_clone.service.dto.ProfileFollowDTO;
import com.su.twitter_clone.web.rest.errors.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProfileResource {

    private final Logger log = LoggerFactory.getLogger(ProfileResource.class);
    private final UserService userService;
    private final ProfileService profileService;

    public ProfileResource(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    /**
     * Get logged in users profile
     * @return Profile or NULL if it doesn't exist in db
     */
    @GetMapping("/profiles/me")
    public ProfileDTO getMyProfile() {
        log.info("getMyProfile was called");
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));

        // can return null .. but highly impossible
        return profileService.getLoggedInUserProfile(user);
    }

    /**
     * Get targeted profile
     * @param id Targeted profiles id
     * @return Profile or NULL if it doesn't exist in db
     */
    @GetMapping("/profiles")
    public ProfileFollowDTO getProfile(@RequestParam(value = "id") int id) {
        log.info("getProfile was called id > " + id);
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        return profileService.getProfile(user, id);
    }

    /**
     * Is the logged in user following this profile
     * @param id profile id to check
     * @return true / false
     */
    @GetMapping("/profiles/following")
    public boolean isFollowing(@RequestParam(value = "id") int id) {
        log.info("isFollowing was called id > " + id);
        User user = userService.getUserWithAuthorities().orElseThrow(() -> new InternalServerErrorException("User could not be found"));
        return profileService.isFollowing(user, id);
    }

}
