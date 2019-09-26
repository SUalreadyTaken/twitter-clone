package com.su.twitter_clone.service;

import com.su.twitter_clone.domain.Profile;
import com.su.twitter_clone.domain.Tweet;
import com.su.twitter_clone.domain.User;
import com.su.twitter_clone.repository.FollowingRepository;
import com.su.twitter_clone.repository.ProfileRepository;
import com.su.twitter_clone.repository.TweetRepository;
import com.su.twitter_clone.service.dto.ProfileFollowDTO;
import com.su.twitter_clone.service.dto.TweetDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.su.twitter_clone.service.converter.ProfileConverter.getProfileFollowDTO;
import static com.su.twitter_clone.service.converter.TweetConverter.getTweetDTO;

@Service
@Transactional
public class SearchService {

    private final Logger log = LoggerFactory.getLogger(SearchService.class);
    private final TweetRepository tweetRepository;
    private final ProfileRepository profileRepository;
    private final FollowingRepository followingRepository;

    public SearchService(TweetRepository tweetRepository, ProfileRepository profileRepository, FollowingRepository followingRepository) {
        this.tweetRepository = tweetRepository;
        this.profileRepository = profileRepository;
        this.followingRepository = followingRepository;
    }

    /**
     * Find all tweets whose content contains searching string
     *
     * @param searching Search string
     * @return List of TweetDTOs
     */
    @Transactional(readOnly = true)
    public List<TweetDTO> searchTweets(String searching) {
        log.info("SearchService.searchTweets containing > " + searching);
        List<Tweet> tweetsList = tweetRepository.findByContentContaining(searching);
        List<TweetDTO> result = new ArrayList<>();
        tweetsList.forEach(t -> result.add(getTweetDTO(t)));
        return result;
    }

    /**
     * Find all profiles whose login or displayName contains searching string
     *
     * @param searching Search string
     * @param user Logged in user
     * @return List of ProfileFollowDTOs
     */
    @Transactional(readOnly = true)
    public List<ProfileFollowDTO> searchProfiles(String searching, User user) {
        log.info("SearchService.searchProfiles containing > " + searching);
        List<Profile> profileList = profileRepository.searchProfiles(searching);
        List<ProfileFollowDTO> result = new ArrayList<>();
        Optional<Profile> myProfile = profileRepository.findFirstByUser(user);
        if (myProfile.isPresent()) {
            List<Integer> followingIds = new ArrayList<>();
            // remove myself
            profileList.removeIf(profile -> profile.getId() == myProfile.get().getId());
            if (myProfile.get().getFollowingCount() > 0) {
                Pageable tmpPageable = PageRequest.of(0, myProfile.get().getFollowingCount());
                followingRepository.findLoggedInUserFollowings(myProfile.get().getId(), tmpPageable).forEach(f -> followingIds.add(f.getFollowingId()));
            }
            if (followingIds.isEmpty()) {
                profileList.forEach(profile -> result.add(getProfileFollowDTO(profile, false)));
            } else {
                for (Profile profile : profileList) {
                    if (followingIds.contains(profile.getId())) {
                        result.add(getProfileFollowDTO(profile, true));
                    } else
                        result.add(getProfileFollowDTO(profile, false));
                }
            }
        }
        return result;
    }

}
