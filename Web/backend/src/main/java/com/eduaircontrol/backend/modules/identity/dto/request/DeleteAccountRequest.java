package com.eduaircontrol.backend.modules.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteAccountRequest {
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
