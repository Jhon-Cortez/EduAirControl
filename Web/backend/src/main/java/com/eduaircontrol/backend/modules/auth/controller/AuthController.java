package com.eduaircontrol.backend.modules.auth.controller;

import com.eduaircontrol.backend.modules.auth.dto.request.ChangePasswordRequest;
import com.eduaircontrol.backend.modules.auth.dto.request.DeleteAccountRequest;
import com.eduaircontrol.backend.modules.auth.dto.request.ForgotPasswordRequest;
import com.eduaircontrol.backend.modules.auth.dto.request.LoginRequest;
import com.eduaircontrol.backend.modules.auth.dto.request.RegisterRequest;
import com.eduaircontrol.backend.modules.auth.dto.request.ResendCodeRequest;
import com.eduaircontrol.backend.modules.auth.dto.request.ResetPasswordRequest;
import com.eduaircontrol.backend.modules.auth.dto.request.VerifyCodeRequest;
import com.eduaircontrol.backend.modules.auth.dto.response.AuthResponse;
import com.eduaircontrol.backend.modules.auth.entity.Users;
import com.eduaircontrol.backend.modules.auth.service.implement.PasswordResetService;
import com.eduaircontrol.backend.modules.auth.service.implement.UserService;
import com.eduaircontrol.backend.modules.security.JwtService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final JwtService jwtService;

    //Registro
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request){
        Users user = userService.register(request);
        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request){

        String token = userService.login(request);

        return new AuthResponse(token);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Si el correo existe, se ha enviado un código de verificación"));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(
            @Valid @RequestBody VerifyCodeRequest request) {
        passwordResetService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(Map.of("message", "Código verificado"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(
                request.getEmail(), request.getCode(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<Map<String, String>> resendCode(
            @Valid @RequestBody ResendCodeRequest request) {
        passwordResetService.resendCode(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Si el correo existe, se ha enviado un nuevo código"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        userService.changePassword(currentUser(authentication), request);
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
    }

    @DeleteMapping("/account")
    public ResponseEntity<Map<String, String>> deleteAccount(
            @Valid @RequestBody DeleteAccountRequest request,
            Authentication authentication) {
        userService.deleteAccount(currentUser(authentication), request.getPassword());
        return ResponseEntity.ok(Map.of("message", "Cuenta eliminada correctamente"));
    }

    private String currentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Sesión requerida");
        }
        return authentication.getName();
    }
}
