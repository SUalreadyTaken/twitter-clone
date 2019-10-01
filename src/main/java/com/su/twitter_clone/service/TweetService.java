package com.su.twitter_clone.service;

import com.su.twitter_clone.domain.Profile;
import com.su.twitter_clone.domain.Tweet;
import com.su.twitter_clone.domain.User;
import com.su.twitter_clone.repository.FollowingRepository;
import com.su.twitter_clone.repository.ProfileIdAndTweetCount;
import com.su.twitter_clone.repository.ProfileRepository;
import com.su.twitter_clone.repository.TweetRepository;
import com.su.twitter_clone.service.dto.TweetDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.su.twitter_clone.service.converter.TweetConverter.getTweetDTO;

@Service
@Transactional
public class TweetService {

    private final Logger log = LoggerFactory.getLogger(TweetService.class);
    private final ProfileRepository profileRepository;
    private final FollowingRepository followingRepository;
    private final TweetRepository tweetRepository;
    private final WsSessionService wsSessionService;

    /**
     * Maximum number of tweets per request
     */
    @Value("${tweets.max.to.receive}")
    private int MAX_TWEETS;

    /**
     * Default page size
     */
    @Value("${tweets.page.to.check.size}")
    private int PAGE_SIZE;

    public TweetService(ProfileRepository profileRepository, FollowingRepository followingRepository, TweetRepository tweetRepository, WsSessionService wsSessionService) {
        this.profileRepository = profileRepository;
        this.followingRepository = followingRepository;
        this.tweetRepository = tweetRepository;
        this.wsSessionService = wsSessionService;
    }

    /**
     * Get this profiles tweets
     * @param profileId Profile id
     * @return List
     */
    @Transactional(readOnly = true)
    public List<TweetDTO> getProfileTweets(int profileId) {
        log.info("TweetService.getProfileTweets was called");
        Pageable tmpPageable = PageRequest.of(0, 1);
        List<ProfileIdAndTweetCount> tmpProfile = new ArrayList<>(profileRepository.findProfileById(profileId, tmpPageable).getContent());
        if (tmpProfile.size() > 0) {
            int tweetCount = tmpProfile.get(0).getTweetsCount();
            if (tweetCount > 0) {
                int resultSize = MAX_TWEETS < tweetCount ? MAX_TWEETS : tweetCount;
                HashSet<Integer> profileIds = new HashSet<>();
                profileIds.add(tmpProfile.get(0).getId());
                return tweetsToReturn(profileIds, resultSize);
            }
        } else {
            log.error("TweetService.getProfileTweets did'nt find profile with id > " +  profileId);

        }
        return Collections.emptyList();
    }

    /**
     * Get this profiles tweets before last tweet
     * @param profileId Profile id
     * @param lastTweetId Last tweets id
     * @param alreadyReceived Number of tweets already received
     * @return List
     */
    public List<TweetDTO> getMoreProfileTweets(int profileId, int lastTweetId, int alreadyReceived) {
        log.info("TweetService.getMoreProfileTweets was called");
        Pageable tmpPageable = PageRequest.of(0, 1);
        List<ProfileIdAndTweetCount> tmpProfile = new ArrayList<>(profileRepository.findProfileById(profileId, tmpPageable).getContent());
        if (tmpProfile.size() > 0) {
            int tweetsToReceive = tmpProfile.get(0).getTweetsCount() - alreadyReceived;
            if (tweetsToReceive > 0) {
                int resultSize = MAX_TWEETS < tweetsToReceive ? MAX_TWEETS : tweetsToReceive;
                HashSet<Integer> profileIds = new HashSet<>();
                profileIds.add(tmpProfile.get(0).getId());
                return getNextPatchOfTweets(profileIds, lastTweetId, resultSize);
            }

        }
        else {
            log.error("TweetService.getProfileTweets did'nt find profile with id > " +  profileId);

        }
        return Collections.emptyList();
    }

    /**
     * Get a list of tweets containing users and his followings tweets
     * @param user Users whose timeline to return
     * @return List<TweetDTO>
     */
    @Transactional(readOnly = true)
    public List<TweetDTO> getTimelineTweets(User user) {
        log.info("TweetService.getTimelineTweets was called");
        Optional<Profile> tmpProfile = profileRepository.findFirstByUser(user);
        if (tmpProfile.isPresent()) {
            Profile profile = tmpProfile.get();
            List<TweetDTO> tweetDTOList = new ArrayList<>();
            List<ProfileIdAndTweetCount> profileList = new ArrayList<>();

            profileList = getProfileIdAndTweetCounts(profile, profileList);

            int maxTweetsCount = getMaxTweetsCount(profileList, profile.getTweetsCount());

            if (maxTweetsCount == 0) {
                return tweetDTOList;
            }


            HashSet<Integer> profileIds = getProfileIds(profile, profileList);

            int resultListSize = MAX_TWEETS < maxTweetsCount ? MAX_TWEETS : maxTweetsCount;

            return tweetsToReturn(profileIds, resultListSize);
        } else {
            log.error("TweetService.getTimelineTweets didn't find profile linked to user > " + user.getLogin());
        }
        return Collections.emptyList();
    }

    /**
     * Get a list of tweets containing users and his followings tweets before the last tweet
     * @param user Users whose timeline to return
     * @param lastTweetId last tweets id
     * @param alreadyReceived number of tweets already in timeline
     * @return List<TweetDTO>
     */
    @Transactional(readOnly = true)
    public List<TweetDTO> getMoreTimelineTweets(User user, int lastTweetId, int alreadyReceived) {
        log.info("TweetService.getNextPatchOfTweets was called");

        Optional<Profile> tmpProfile = profileRepository.findFirstByUser(user);
        if (tmpProfile.isPresent()) {
            Profile profile = tmpProfile.get();
            List<TweetDTO> tweetDTOList = new ArrayList<>();
            List<ProfileIdAndTweetCount> profileList = new ArrayList<>();

            profileList = getProfileIdAndTweetCounts(profile, profileList);

            int maxTweetsCount = getMaxTweetsCount(profileList, profile.getTweetsCount() - alreadyReceived);

            // no more tweets to be found.. return empty list
            if (maxTweetsCount <= 0) {
                return tweetDTOList;
            }

            HashSet<Integer> profileIds = getProfileIds(profile, profileList);
            int resultListSize = MAX_TWEETS < maxTweetsCount ? MAX_TWEETS : maxTweetsCount;
            return getNextPatchOfTweets(profileIds, lastTweetId, resultListSize);

        } else {
            log.error("TweetService.getMoreTimelineTweets didn't find profile linked to user > " + user.getLogin());
        }
        return Collections.emptyList();
    }

    private List<ProfileIdAndTweetCount> getProfileIdAndTweetCounts(Profile profile, List<ProfileIdAndTweetCount> profileList) {
        if (profile.getFollowingCount() > 0) {
            Pageable tmpPageable = PageRequest.of(0, profile.getFollowingCount());
            profileList = profileRepository.findFollowingsProfileIdsAndTweetCount(profile, tmpPageable).getContent();
        }
        return profileList;
    }

    private HashSet<Integer> getProfileIds(Profile profile, List<ProfileIdAndTweetCount> profileList) {
        HashSet<Integer> profileIds = new HashSet<>();
        profileList.forEach(p -> {
            if (p.getTweetsCount() > 0) {
                profileIds.add(p.getId());
            }
        });
        profileIds.add(profile.getId());
        return profileIds;
    }

    private int getMaxTweetsCount(List<ProfileIdAndTweetCount> profileList, int tweetsCount) {
        int maxTweetsCount = tweetsCount;
        for (ProfileIdAndTweetCount p : profileList) {
            maxTweetsCount += p.getTweetsCount();
            if (maxTweetsCount > MAX_TWEETS)
                break;
        }
        return maxTweetsCount;
    }

    /**
     * Get a list of tweets
     * @param profileIds tweet.profile.id must be in this set
     * @param resultListSize size of the list to be created
     * @return List<TweetDTO>
     */
    private List<TweetDTO> tweetsToReturn(HashSet<Integer> profileIds, int resultListSize) {
        log.info("TweetService.tweetsToReturn was called");
        List<TweetDTO> tweetDTOList = new ArrayList<>();
        int startPage = 0;
        while (resultListSize > 0) {
            Pageable pageable = PageRequest.of(startPage, PAGE_SIZE, Sort.Direction.DESC, "id");
            List<Tweet> tweetList = tweetRepository.findAll(pageable).getContent();
            if (!tweetList.isEmpty()) {
                resultListSize = getRightTweets(profileIds, resultListSize, tweetDTOList, tweetList);
            } else {
                break;
            }
            startPage++;
        }

        return tweetDTOList;
    }

    /**
     * Get a list of tweets before the last tweet
     * @param profileIds tweet.profile.id must be in this sec
     * @param lastTweetId last tweets id
     * @param resultListSize size of the list to be created
     * @return List<TweetDTO>
     */
    @Transactional(readOnly = true)
    public List<TweetDTO> getNextPatchOfTweets(HashSet<Integer> profileIds, int lastTweetId, int resultListSize) {
        log.info("TweetService.getNextPatchOfTweets was called");
        List<TweetDTO> tweetDTOList = new ArrayList<>();
        int startPage = 0;
        while (resultListSize > 0) {
            Pageable pageable = PageRequest.of(startPage, PAGE_SIZE, Sort.Direction.DESC, "id");
            List<Tweet> tweetList = tweetRepository.findAllSinceLast(pageable, lastTweetId).getContent();
            if (!tweetList.isEmpty()) {
                resultListSize = getRightTweets(profileIds, resultListSize, tweetDTOList, tweetList);
            } else {
                break;
            }
            startPage++;
        }
        return tweetDTOList;

    }

    private int getRightTweets(HashSet<Integer> profileIds, int resultListSize, List<TweetDTO> tweetDTOList, List<Tweet> tweetList) {
        for (Tweet tweet : tweetList) {
            if (profileIds.contains(tweet.getProfile().getId())) {
                tweetDTOList.add(getTweetDTO(tweet));
                resultListSize--;
                if (resultListSize == 0) {
                    break;
                }
            }
        }
        return resultListSize;
    }

    /**
     * Saves tweet to db. Sends sendNewTweetMessage to wsSessionService with logged in users followers id list.
     * Increases users tweet count by 1.
     * @param user Logged in user
     * @param content Tweets content
     * @return TweetDTO. Null if cant find logged in users profile
     */
    public TweetDTO postTweet(User user, String content) {
        log.info("TweetService.postTweet was called");
        Optional<Profile> tmpProfile = profileRepository.findFirstByUser(user);
        if (tmpProfile.isPresent()) {
            Profile profile = tmpProfile.get();
            Tweet tweet = new Tweet(profile, content, LocalDateTime.now());
            tweetRepository.save(tweet);
            profile.setTweetsCount(profile.getTweetsCount() + 1);
            if (profile.getFollowersCount() > 0) {
                Pageable pageable = PageRequest.of(0, profile.getFollowersCount());
                List<Integer> followerIds = followingRepository.findLoggedInUserFollowerIds(profile.getId(), pageable).getContent();
                wsSessionService.sendNewTweet(followerIds);
            }
            return getTweetDTO(tweet);
        }
        return null;
    }

    // todo maybe turn to boolean. if tweet exists
    /**
     * If tweet exists, deletes from db and decreases users tweet count by 1
     * @param id Tweets id
     * @param user Logged in user
     */
    public void deleteTweet(int id, User user) {
        log.info("TweetService.deleteTweet was called  tweet id > " + id + " user > " + user.getLogin());
        Optional<Profile> profile = profileRepository.findFirstByUser(user);
        if (profile.isPresent()) {
            if (tweetRepository.existsById(id)) {
                profile.get().setTweetsCount(profile.get().getTweetsCount() - 1);
                tweetRepository.deleteById(id);
            }
        }
    }

}
