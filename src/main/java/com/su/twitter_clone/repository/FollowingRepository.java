package com.su.twitter_clone.repository;

import com.su.twitter_clone.domain.Following;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowingRepository extends JpaRepository<Following, Integer> {


    @Query(value = " SELECT id as id,  profile_id as followerId, following_id as followingId FROM following" +
            " WHERE profile_id = :id", nativeQuery = true)
    Slice<FollowingItem> findLoggedInUserFollowings(@Param("id") int id, Pageable pageable);

    @Query(value = " SELECT profile_id FROM following" +
        " WHERE following_id = :id", nativeQuery = true)
    Slice<Integer> findLoggedInUserFollowerIds(@Param("id") int id, Pageable pageable);


    @Modifying
    @Query(value = " INSERT INTO following (profile_id, following_id) VALUES (:profileId,:followingId) ", nativeQuery = true)
    void setFollow(@Param("profileId") int myId, @Param("followingId") int id);

    @Modifying
    @Query(value = "DELETE FROM following WHERE profile_id = :profileId AND following_id = :followingId", nativeQuery = true)
    void deleteFollow(@Param("profileId") int myId, @Param("followingId") int id);

    // dunoo gives syntax error so doing it with isFollowing and setFollowing
    //    @Modifying
//    @Query(value = " INSERT INTO following (profile_id, following_id) VALUES (:profileId,:followingId) WHERE NOT EXISTS" +
//        " (SELECT * FROM following WHERE profile_id = :profileId AND following_id = :followingId) ", nativeQuery = true)
//    void setFollow2(@Param("profileId") int myId, @Param("followingId") int id);

    @Query(value = "SELECT CASE WHEN count(*) > 0 THEN true ELSE false END FROM following  WHERE profile_id = :myId AND following_id = :id", nativeQuery = true)
    boolean isFollowing(@Param("myId") int myId, @Param("id") int id);

}
