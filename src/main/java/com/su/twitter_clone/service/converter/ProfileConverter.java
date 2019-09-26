package com.su.twitter_clone.service.converter;

import com.su.twitter_clone.domain.Profile;
import com.su.twitter_clone.service.dto.ProfileDTO;
import com.su.twitter_clone.service.dto.ProfileFollowDTO;

public class ProfileConverter {


    public static ProfileDTO getProfileDTO(Profile profile) {
        return new ProfileDTO(profile.getId(), profile.getDisplayName(), profile.getUser().getLogin(), profile.getDescription(),
                              profile.getFollowingCount(), profile.getFollowersCount(), profile.getTweetsCount());
    }

    // not using it
//    public static ProfileFollowDTO getProfileFollowDTO(ProfileDTO profileDTO, boolean following) {
//        return new ProfileFollowDTO(profileDTO.getId(), profileDTO.getDisplayName(), profileDTO.getLogin(), profileDTO.getDescription(),
//                                    profileDTO.getFollowingCount(), profileDTO.getFollowersCount(), profileDTO.getTweetsCount(), following);
//    }

    public static ProfileFollowDTO getProfileFollowDTO(Profile profile, boolean following) {
        return new ProfileFollowDTO(profile.getId(), profile.getDisplayName(), profile.getUser().getLogin(), profile.getDescription(),
                                    profile.getFollowingCount(), profile.getFollowersCount(), profile.getTweetsCount(), following);
    }
}
