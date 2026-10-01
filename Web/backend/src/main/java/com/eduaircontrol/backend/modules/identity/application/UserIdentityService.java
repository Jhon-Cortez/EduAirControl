package com.eduaircontrol.backend.modules.identity.application;

import com.eduaircontrol.backend.modules.identity.repository.UserRepository;
import com.eduaircontrol.backend.shared.contract.UserIdentityPort;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserIdentityService implements UserIdentityPort {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<UUID> idByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.trim()).map(user -> user.getId());
    }
}
