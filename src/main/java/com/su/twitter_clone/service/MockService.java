package com.su.twitter_clone.service;

import com.su.twitter_clone.domain.*;
import com.su.twitter_clone.repository.*;
import com.su.twitter_clone.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * To pre populated db with random users, followings, tweets
 */
@Service
@Transactional
public class MockService {

    private final Logger log = LoggerFactory.getLogger(MockService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final ProfileRepository profileRepository;
    private final FollowingRepository followingRepository;
    private final TweetRepository tweetRepository;

    public MockService(ProfileRepository profileRepository, FollowingRepository followingRepository,
                       TweetRepository tweetRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository,
                       UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.followingRepository = followingRepository;
        this.tweetRepository = tweetRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates new users
     * Login and password will be the same
     * Display name will be reverse of login
     * Test0 will always be created if not existing
     *
     * @param count amount of new users to create
     */
    public void createUser(int count) {

        List<String> singleList;
        String[] words = null;

        try (Stream<String> stream = Files.lines(Paths.get("random_words.txt"))) {
            singleList = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
            words = singleList.get(0).split(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //existing logins .. cant be duplicates
        List<String> existingLogins = new ArrayList<>();
        userRepository.findAll().forEach(user -> existingLogins.add(user.getLogin()));

        if (!existingLogins.contains("test0")) {
            createTest0();
        }

        if (words != null) {
            int wordsLength = words.length;
            for (int i = 0; i < count; i++) {
                // create login and display name (reverse of login)
                int randomNum = ThreadLocalRandom.current().nextInt(0, wordsLength);
                String login = words[randomNum];
                if (existingLogins.contains(login)) {
                    log.warn("existing  login 1st try");
                    int rand = ThreadLocalRandom.current().nextInt(0, wordsLength);
                    login = login + rand;
                    if (existingLogins.contains(login)) {
                        log.warn("existing  login 2nd try gonna skip");
                        continue;
                    }

                }
                existingLogins.add(login);
                String displayName = new StringBuilder(login).reverse().toString();

                // create user
                User newUser = new User();
                String encryptedPassword = passwordEncoder.encode(login);
                newUser.setLogin(login);
                newUser.setPassword(encryptedPassword);
                newUser.setFirstName(login);
                newUser.setLastName(login);
                newUser.setEmail(login + "@" + displayName + ".com");
                //        newUser.setImageUrl(userDTO.getImageUrl());
                newUser.setLangKey("en");
                newUser.setActivated(true);
                Set<Authority> authorities = new HashSet<>();
                authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
                newUser.setAuthorities(authorities);
                userRepository.save(newUser);

                // create profile
                Profile profile = new Profile(newUser, displayName, "Some random description", 0, 0, 0);
                profileRepository.save(profile);
            }
        }

        log.debug("Users and profiles created");

    }

    private void createTest0() {
        // create user
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode("test0");
        newUser.setLogin("test0");
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName("test0");
        newUser.setLastName("test0");
        newUser.setEmail("test0@test.com");
        //        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey("en");
        newUser.setActivated(true);
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        // create profile
        Profile profile = new Profile(newUser, "test0", "Some random description", 0, 0, 0);
        profileRepository.save(profile);
    }


    /**
     * Every user follows random amount of new users
     *
     * @param count max amount of new followings
     */
    public void createFollowing(int count) {

        log.debug("Creating followings");

        List<Profile> profileList = profileRepository.findAll();

        Map<Profile, Integer> followingMap = new HashMap<>();
        Map<Profile, Integer> followersMap = new HashMap<>();

        for (Profile profile : profileList) {
            followingMap.put(profile, profile.getFollowingCount());
            followersMap.put(profile, profile.getFollowersCount());
        }

        int profilesSize = profileList.size();
        List<Following> followingList = new ArrayList<>();
        for (Profile profile : profileList) {
            //already following from previous..
            List<Integer> alreadyFollowingId = new ArrayList<>();
            alreadyFollowingId.add(profile.getId());
            if (profile.getFollowingCount() > 0) {
                Pageable tmpPageable = PageRequest.of(0, profile.getFollowingCount());
                profileRepository.findFollowings(profile, tmpPageable).forEach(p -> alreadyFollowingId.add(p.getId()));
            }
            int randNewFollowing = ThreadLocalRandom.current().nextInt(0, count);
            for (int i = 0; i < randNewFollowing; i++) {
                int randomNum = ThreadLocalRandom.current().nextInt(0, profilesSize);
                Profile gonnaFollow = profileList.get(randomNum);
                if (!alreadyFollowingId.contains(gonnaFollow.getId())) {
                    followingList.add(new Following(profile, gonnaFollow));
                    followingMap.put(profile, followingMap.get(profile) + 1);
                    followersMap.put(gonnaFollow, followersMap.get(gonnaFollow) + 1);
                    alreadyFollowingId.add(gonnaFollow.getId());
                }
            }
        }

        for (Profile profile : profileList) {
            profile.setFollowingCount(followingMap.get(profile));
            profile.setFollowersCount(followersMap.get(profile));
        }

        // so its mixed up
        followingList.sort(Comparator.comparing(following -> following.getFollowingUser().getId()));
        followingRepository.saveAll(followingList);

    }

    /**
     * Takes a random user and creates a new tweet. Day will be current day.
     * Hour will be same if there's previous tweets or last tweet was made at the same day.
     * Hour will be current - 1 if no previous tweets or last tweet is at least day old
     * Minutes will be greater than last tweets or 0-60 if hour if different of 0-5 if no previous tweets or if different day
     * Seconds will be random or greater than lasts
     *
     * @param count amount of tweets to be created
     */
    public void createTweets(int count) {
        log.debug("Creating tweets");

        List<String> lyricLines = new ArrayList<>();
        List<Profile> userList = profileRepository.findAll();

        try (Stream<String> stream = Files.lines(Paths.get("meteora_lyrics.txt"))) {
            lyricLines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int userListSIze = userList.size();
        int currentHour = LocalDateTime.now().getHour();
        int currentMinute = LocalDateTime.now().getMinute();
        int currentSecond = LocalDateTime.now().getSecond();
        Random random = new Random();

        Optional<Tweet> lastTweet = tweetRepository.findFirstByOrderByIdDesc();
        List<Tweet> tweetsToCreate = new ArrayList<>();

        if (lastTweet.isPresent() && lastTweet.get().getTime().getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
            Tweet tweet = lastTweet.get();
            // +1 so it can get past the high seconds
            int minMinutes = tweet.getTime().getMinute() + 1;
            int minSeconds = tweet.getTime().getSecond();

            if (currentHour > tweet.getTime().getHour()) {
                minMinutes = 0;
                currentMinute = 60;
                minSeconds = 0;
                currentSecond = 60;
            } else if (minMinutes < currentMinute - 1 && minMinutes != currentMinute - 1) {
                minSeconds = 0;
                currentSecond = 60;
            }

            for (int i = 0; i < count; i++) {
                // take random user from the userList
                Profile profile = userList.get(ThreadLocalRandom.current().nextInt(0, userListSIze));
                String line = lyricLines.get(ThreadLocalRandom.current().nextInt(0, lyricLines.size()));
                LocalDateTime randTime = LocalDateTime.of(LocalDate.now(),
                                                          LocalTime.of(currentHour,
                                                                       getRand(minMinutes, currentMinute),
                                                                       getRand(minSeconds, currentSecond),
                                                                       random.nextInt(99) * 10000000));
                Tweet t = new Tweet(profile, line, randTime);
                profile.setTweetsCount(profile.getTweetsCount() + 1);
                tweetsToCreate.add(t);
            }
        } else {
            //no previous tweets or day before
            log.info("No previous tweets or different day");
            // so tweets will be 1 hour earlier
            if (currentHour >= 1) {
                currentHour--;
            } else {
                currentHour = 23;
            }
            for (int i = 0; i < count; i++) {
                // take random user from the userList
                Profile profile = userList.get(ThreadLocalRandom.current().nextInt(0, userListSIze));
                String line = lyricLines.get(ThreadLocalRandom.current().nextInt(0, lyricLines.size()));
                LocalDateTime randTime = LocalDateTime.of(LocalDate.now(),
                                                          LocalTime.of(currentHour,
                                                                       getRand(0, 5),
                                                                       random.nextInt(60),
                                                                       random.nextInt(99) * 10000000));
                Tweet t = new Tweet(profile, line, randTime);
                profile.setTweetsCount(profile.getTweetsCount() + 1);
                tweetsToCreate.add(t);
            }
        }

        tweetsToCreate.sort(Comparator.comparing(Tweet::getTime));
        tweetsToCreate.forEach(tweetRepository::save);
    }

    private int getRand(int min, int current) {
        int rand;
        if (min > current)
            return min;
        if (current == 0)
            return 0;
        if (min == current || min == (current - 1)) {
            rand = current;
        } else {
            rand = ThreadLocalRandom.current().nextInt(min, current - 1);
        }
        return rand;
    }

}
