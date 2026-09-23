package com.eduaircontrol.backend.modules.auth.service.implement;

import com.eduaircontrol.backend.modules.auth.entity.PasswordResetToken;
import com.eduaircontrol.backend.modules.auth.entity.Users;
import com.eduaircontrol.backend.modules.auth.repository.PasswordResetTokenRepository;
import com.eduaircontrol.backend.modules.auth.repository.UserRepository;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration CODE_TTL = Duration.ofMinutes(15);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            tokenRepository.deleteByEmail(email);
            String code = generateCode();
            PasswordResetToken token = PasswordResetToken.builder()
                    .email(email)
                    .codeHash(passwordEncoder.encode(code))
                    .expiresAt(Instant.now().plus(CODE_TTL))
                    .used(false)
                    .attempts(0)
                    .createdAt(Instant.now())
                    .build();
            tokenRepository.save(token);
            emailService.sendPasswordResetCode(email, code);
        });
    }

    @Transactional
    public void resendCode(String email) {
        forgotPassword(email);
    }

    @Transactional
    public void verifyCode(String email, String code) {
        validateCode(email, code);
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        PasswordResetToken token = validateCode(email, code);
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);
    }

    private PasswordResetToken validateCode(String email, String code) {
        PasswordResetToken token = tokenRepository
                .findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido o expirado"));

        if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido o expirado");
        }
        if (token.getAttempts() >= MAX_ATTEMPTS) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Demasiados intentos. Solicita un nuevo código");
        }
        if (!passwordEncoder.matches(code, token.getCodeHash())) {
            token.setAttempts(token.getAttempts() + 1);
            tokenRepository.save(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido o expirado");
        }
        return token;
    }

    private String generateCode() {
        int bound = (int) Math.pow(10, CODE_LENGTH);
        int value = RANDOM.nextInt(bound);
        return String.format("%0" + CODE_LENGTH + "d", value);
    }
}
