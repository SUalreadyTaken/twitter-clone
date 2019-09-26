package com.su.twitter_clone.repository;

import com.su.twitter_clone.domain.Profile;
import com.su.twitter_clone.domain.Tweet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TweetRepository extends CrudRepository<Tweet, Integer> {
    List<Tweet> findByProfileId(int id);

    List<Tweet> findByProfile(Profile profile);

    Optional<Tweet> findFirstByOrderByIdDesc();

    List<Tweet> findByContentContaining(String tweetContent);

    Slice<Tweet> findAll(Pageable pageable);

    @Query( value = "SELECT * FROM tweets WHERE id < :last_id",
            nativeQuery = true)
    Slice<Tweet> findAllSinceLast(Pageable pageable, @Param("last_id") int lastId);

}
