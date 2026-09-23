package com.eduaircontrol.backend.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteAccountRequest {
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
