package com.eduaircontrol.backend.modules.identity.service;

import com.eduaircontrol.backend.modules.identity.dto.request.ProfileUpdateRequest;
import com.eduaircontrol.backend.modules.identity.dto.response.ProfileResponse;
import com.eduaircontrol.backend.modules.identity.entity.User;
import com.eduaircontrol.backend.modules.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileResponse get(String email) {
        return ProfileResponse.from(requireUser(email));
    }

    @Transactional
    public ProfileResponse update(String email, ProfileUpdateRequest request) {
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && !request.getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El correo de la cuenta no se puede cambiar aquí");
        }
        User user = requireUser(email);
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setName(request.getFullName().trim());
        }
        user.setTitle(trimToNull(request.getTitle()));
        user.setPhone(trimToNull(request.getPhone()));
        user.setLocation(trimToNull(request.getLocation()));
        user.setAvatar(trimToNull(request.getAvatar()));
        return ProfileResponse.from(userRepository.save(user));
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión inválida"));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
