package com.eduaircontrol.backend.modules.monitoring.application;

import com.eduaircontrol.backend.modules.monitoring.entity.QualityFlag;
import com.eduaircontrol.backend.modules.monitoring.repository.QualityFlagRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class QualityFlagService {

    private final QualityFlagRepository qualityFlagRepository;

    @Transactional(readOnly = true)
    public UUID goodFlagId() {
        return qualityFlagRepository.findByCode("GOOD")
                .map(QualityFlag::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Flag de calidad GOOD no sembrada"));
    }
}
