package com.eduaircontrol.backend.modules.identity.bootstrap;

import com.eduaircontrol.backend.modules.identity.entity.Role;
import com.eduaircontrol.backend.modules.identity.entity.User;
import com.eduaircontrol.backend.modules.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea el primer administrador del sistema a partir de variables de entorno
 * (ADMIN_EMAIL / ADMIN_PASSWORD / ADMIN_COMPANY_CODE). Si no estan definidas o el
 * usuario ya existe, no hace nada.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrapRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.admin.company-code:}")
    private String adminCompanyCode;

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            log.info("ADMIN_EMAIL/ADMIN_PASSWORD no definidos: se omite la creacion del administrador inicial");
            return;
        }
        String email = adminEmail.trim();
        if (userRepository.existsByEmail(email)) {
            log.info("El administrador inicial ya existe: {}", email);
            return;
        }
        User admin = User.builder()
                .name(email)
                .email(email)
                .password(passwordEncoder.encode(adminPassword))
                .companyCode(isBlank(adminCompanyCode) ? null : adminCompanyCode.trim())
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
        log.info("Administrador inicial creado: {}", email);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
