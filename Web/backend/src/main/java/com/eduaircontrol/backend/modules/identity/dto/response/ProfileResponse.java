package com.eduaircontrol.backend.modules.identity.dto.response;

import com.eduaircontrol.backend.modules.identity.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {

    private String fullName;
    private String email;
    private String title;
    private String phone;
    private String location;
    private String avatar;

    public static ProfileResponse from(User user) {
        return ProfileResponse.builder()
                .fullName(user.getName())
                .email(user.getEmail())
                .title(user.getTitle())
                .phone(user.getPhone())
                .location(user.getLocation())
                .avatar(user.getAvatar())
                .build();
    }
}
