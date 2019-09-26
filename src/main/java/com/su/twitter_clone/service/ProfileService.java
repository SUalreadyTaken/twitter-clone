package com.su.twitter_clone.service;

import com.su.twitter_clone.domain.Profile;
import com.su.twitter_clone.domain.User;
import com.su.twitter_clone.repository.FollowingRepository;
import com.su.twitter_clone.repository.ProfileRepository;
import com.su.twitter_clone.service.converter.ProfileConverter;
import com.su.twitter_clone.service.dto.ProfileDTO;
import com.su.twitter_clone.service.dto.ProfileFollowDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.su.twitter_clone.service.converter.ProfileConverter.getProfileFollowDTO;

@Service
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final FollowingRepository followingRepository;

    public ProfileService(ProfileRepository profileRepository, FollowingRepository followingRepository) {
        this.profileRepository = profileRepository;
        this.followingRepository = followingRepository;
    }

    /**
     * Get logged in users ProfileDTO
     * @param user Logged in user
     * @return Logged in users ProfileDTO or null if it doesn't exist
     */
    @Transactional(readOnly = true)
    public ProfileDTO getLoggedInUserProfile(User user) {
        Optional<Profile> profile = profileRepository.findFirstByUserId(user.getId());
        return profile.map(ProfileConverter::getProfileDTO).orElse(null);
    }

    /**
     * Get targeted profiles ProfileFollowDTO
     * @param user Logged in user
     * @param id Targeted profile id
     * @return Targeted profiles ProfileFollowDTO or null if profiles doesn't exist
     */
    @Transactional(readOnly = true)
    public ProfileFollowDTO getProfile(User user, int id) {
        Optional<Profile> profile = profileRepository.findById(id);
        Optional<Profile> myProfile = profileRepository.findFirstByUser(user);
        if (profile.isPresent() && myProfile.isPresent()) {
            boolean following = followingRepository.isFollowing(myProfile.get().getId(), id);
            return following ? getProfileFollowDTO(profile.get(), true) : getProfileFollowDTO(profile.get(), false);
        }
        return null;
    }

    /**
     * Check if logged in user is following targeted profile
     * @param user Logged in user
     * @param id Targeted profiles id
     * @return true / false
     */
    @Transactional(readOnly = true)
    public boolean isFollowing(User user, int id) {
        Optional<Profile> myProfile = profileRepository.findFirstByUser(user);
        if (myProfile.isPresent()) {
            int myId = myProfile.get().getId();
            return followingRepository.isFollowing(myId, id);
        }
        return false;
    }
}
