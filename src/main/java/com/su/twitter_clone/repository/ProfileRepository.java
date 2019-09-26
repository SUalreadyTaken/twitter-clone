package com.su.twitter_clone.repository;

import com.su.twitter_clone.domain.Profile;
import com.su.twitter_clone.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findFirstByUserId(long id);
    Optional<Profile> findFirstByUser(User user);


    /**
     * Find profiles who this profile follows. Select only profileId and tweetCount
     * @param profile Follow.profile
     */
    @Query(value = "SELECT p.id as id, p.tweetsCount as tweetsCount FROM Profile p " +
        "INNER JOIN Following f ON p.id = f.followingUser " +
        "INNER JOIN FETCH User u ON p.user = u.id " +
        "WHERE f.profile = :profile")
    Slice<ProfileIdAndTweetCount> findFollowingsProfileIdsAndTweetCount(@Param("profile") Profile profile, Pageable pageable);

    @Query(value = "SELECT p.id as id, p.tweetsCount as tweetsCount FROM Profile p WHERE p.id = :id")
    Slice<ProfileIdAndTweetCount> findProfileById(@Param("id") int profileId, Pageable pageable);

    /**
     * Find profiles who this profile follows
     * @param profile Follow.profile
     */
    @Query(value = "SELECT p FROM Profile p " +
        "INNER JOIN Following f ON p.id = f.followingUser " +
        "INNER JOIN FETCH User u ON p.user = u.id " +
        "WHERE f.profile = :profile ")
    Slice<Profile> findFollowings(@Param("profile") Profile profile, Pageable pageable);

    /**
     * Find profiles who follow this profile
     * @param profile Follow.followingUser
     */
    @Query(value = "SELECT p FROM Profile p " +
        "INNER JOIN Following f ON p.id = f.profile " +
        "INNER JOIN FETCH User u ON p.user = u.id " +
        "WHERE f.followingUser = :profile ")
    Slice<Profile> findFollowers(@Param("profile") Profile profile, Pageable pageable);

    /**
     * Find profiles where display name contains search string or user.login contains..
     * @param search containing string
     */
    @Query(value = "SELECT p FROM Profile p INNER JOIN FETCH User u ON p.user = u.id " +
        "WHERE p.displayName LIKE %:search% OR u.login LIKE %:search%")
    List<Profile> searchProfiles(@Param("search") String search);
}

