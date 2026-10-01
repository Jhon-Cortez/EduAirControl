package com.eduaircontrol.backend.modules.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {

    @Size(max = 150)
    private String fullName;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 100)
    private String title;

    @Size(max = 30)
    private String phone;

    @Size(max = 120)
    private String location;

    private String avatar;
}
