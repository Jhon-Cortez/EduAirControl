package com.eduaircontrol.backend.modules.aula.repository;

import com.eduaircontrol.backend.modules.aula.entity.Aula;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AulaRepository extends JpaRepository<Aula, Long> {
    Optional<Aula> findByCodigoAula(String codigoAula);
    boolean existsByCodigoAula(String codigoAula);
}
