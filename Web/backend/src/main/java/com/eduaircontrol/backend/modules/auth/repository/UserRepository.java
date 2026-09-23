package com.eduaircontrol.backend.modules.auth.repository;

import com.eduaircontrol.backend.modules.auth.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
}
