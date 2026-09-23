package com.eduaircontrol.backend.modules.auth.service.implement;

import com.eduaircontrol.backend.modules.auth.dto.request.ChangePasswordRequest;
import com.eduaircontrol.backend.modules.auth.dto.request.LoginRequest;
import com.eduaircontrol.backend.modules.auth.dto.request.RegisterRequest;
import com.eduaircontrol.backend.modules.auth.entity.Role;
import com.eduaircontrol.backend.modules.auth.entity.Users;
import com.eduaircontrol.backend.modules.auth.repository.UserRepository;
import com.eduaircontrol.backend.modules.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Users register(RegisterRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        Users user = Users.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .companyCode(request.getCompanyCode())
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }

    public String login(LoginRequest request){
        Users user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        if (user.getCompanyCode() != null && !user.getCompanyCode().equals(request.getCompanyCode())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        return jwtService.generateToken(user);
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión inválida"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La contraseña actual es incorrecta");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void deleteAccount(String email, String password) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión inválida"));

        if (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La contraseña es incorrecta");
        }

        userRepository.delete(user);
    }
}
