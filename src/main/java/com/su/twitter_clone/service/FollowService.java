package com.su.twitter_clone.service;

import com.su.twitter_clone.domain.Profile;
import com.su.twitter_clone.repository.FollowingRepository;
import com.su.twitter_clone.repository.ProfileRepository;
import com.su.twitter_clone.service.dto.ProfileFollowDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.su.twitter_clone.service.converter.ProfileConverter.getProfileFollowDTO;

@Service
@Transactional
public class FollowService {

    private final Logger log = LoggerFactory.getLogger(FollowService.class);
    private final FollowingRepository followingRepository;
    private final ProfileRepository profileRepository;

    public FollowService(FollowingRepository followingRepository, ProfileRepository profileRepository) {
        this.followingRepository = followingRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Find profiles followers. Need myProfile to create ProfileFollowDTO, is the logged in user following or not this profile.
     * @param myProfile Logged in profile
     * @param id Profiles id of whose followers to find
     * @return ProfileFollowDTO list of followers
     */
    @Transactional(readOnly = true)
    public List<ProfileFollowDTO> getFollowers(Profile myProfile, int id) {
        log.info("FollowService.getFollowers");
        Optional<Profile> p = profileRepository.findById(id);
        if (p.isPresent() && p.get().getFollowersCount() > 0) {
            Pageable tmpPageable = PageRequest.of(0, p.get().getFollowersCount());
            List<Profile> profileList = profileRepository.findFollowers(p.get(), tmpPageable).getContent();
            return createProfileFollowDTO(myProfile, profileList);
        }
        return Collections.emptyList();
    }

    /**
     * Find profiles followings. Need myProfile to create ProfileFollowDTO, is the logged in user following or not this profile.
     * @param myProfile Logged in profile
     * @param id Profiles id of whose followers to find
     * @return ProfileFollowDTO list of followers
     */
    @Transactional(readOnly = true)
    public List<ProfileFollowDTO> getFollowings(Profile myProfile, int id) {
        log.info("FollowService.getFollowings");
        Optional<Profile> p = profileRepository.findById(id);
        if (p.isPresent() && p.get().getFollowingCount() > 0) {
            Pageable tmpPageable = PageRequest.of(0, p.get().getFollowingCount());
            List<Profile> profileList = profileRepository.findFollowings(p.get(), tmpPageable).getContent();
            return createProfileFollowDTO(myProfile, profileList);
        }
        return Collections.emptyList();
    }

    /**
     * Create ProfileFollowDTO. Find logged in users followings and add true or false.
     * @param myProfile Logged in profile
     * @param profileList profileList
     * @return list of Profiles converted to ProfileFollowDTO
     */
    @Transactional(readOnly = true)
    public List<ProfileFollowDTO> createProfileFollowDTO(Profile myProfile, List<Profile> profileList) {
        List<ProfileFollowDTO> result = new ArrayList<>();
        if (myProfile.getFollowingCount() > 0) {
            // profile ids that i follow
            List<Integer> followingIds = new ArrayList<>();
            Pageable tmpPageable = PageRequest.of(0, myProfile.getFollowingCount());
            followingRepository.findLoggedInUserFollowings(myProfile.getId(), tmpPageable).forEach(f -> followingIds.add(f.getFollowingId()));
            for (Profile profile : profileList) {
                if (followingIds.contains(profile.getId())) {
                    result.add(getProfileFollowDTO(profile, true));
                } else {
                    result.add(getProfileFollowDTO(profile, false));

                }
            }
        } else {
            profileList.forEach(profile -> result.add(getProfileFollowDTO(profile, false)));
        }
        // reverse so latest followers/followings are displayed 1st
        Collections.reverse(result);
        return  result;
    }

    // todo send to WsSessionService targeting id
    /**
     * Create following of myProfile id, target id
     * @param myProfile Logged in users profile
     * @param id Target profiles id
     */
    public void setFollow(Profile myProfile, int id) {
        log.info("FollowService.setFollow to id >> " + id + " | myId >> " + myProfile.getId());
        boolean following = followingRepository.isFollowing(myProfile.getId(), id);
        if (!following) {
            followingRepository.setFollow(myProfile.getId(), id);
            myProfile.setFollowingCount(myProfile.getFollowingCount() + 1);
            Profile otherProfile = profileRepository.findById(id).orElse(null);
            if (otherProfile != null) {
                otherProfile.setFollowersCount(otherProfile.getFollowersCount() + 1);
                profileRepository.save(otherProfile);
            }
            profileRepository.save(myProfile);
        }

    }

    // todo send to WsSessionService targeting id
    /**
     * Delete following of myProfile id, target id
     * @param myProfile Logged in users profile
     * @param id Target profiles id
     */
    public void deleteFollow(Profile myProfile, int id) {
        log.info("FollowService.deleteFollow of id >> " + id + " | myId >> " + myProfile.getId());
        boolean following = followingRepository.isFollowing(myProfile.getId(), id);
        if (following) {
            followingRepository.deleteFollow(myProfile.getId(), id);
            myProfile.setFollowingCount(myProfile.getFollowingCount() - 1);
            Profile otherProfile = profileRepository.findById(id).orElse(null);
            if (otherProfile != null) {
                otherProfile.setFollowersCount(otherProfile.getFollowersCount() - 1);
                profileRepository.save(otherProfile);
            }
            profileRepository.save(myProfile);

        }
    }
}
