package com.eduaircontrol.backend.modules.identity.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private String role;
}
