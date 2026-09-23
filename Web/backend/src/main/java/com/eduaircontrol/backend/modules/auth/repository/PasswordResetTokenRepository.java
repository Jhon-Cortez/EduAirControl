package com.eduaircontrol.backend.modules.auth.repository;

import com.eduaircontrol.backend.modules.auth.entity.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(String email);
    void deleteByEmail(String email);
}
