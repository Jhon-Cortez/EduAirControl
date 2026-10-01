package com.eduaircontrol.backend.modules.sensors.application;

import com.eduaircontrol.backend.modules.sensors.entity.SensorInstallation;
import com.eduaircontrol.backend.modules.sensors.repository.SensorInstallationRepository;
import com.eduaircontrol.backend.modules.sensors.repository.SensorVariableRepository;
import com.eduaircontrol.backend.shared.contract.InstallationPort;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstallationService implements InstallationPort {

    private final SensorInstallationRepository installationRepository;
    private final SensorVariableRepository variableRepository;

    @Override
    @Transactional
    public void retireByEnvironment(UUID environmentId) {
        List<SensorInstallation> installations =
                installationRepository.findActive(environmentId);
        Instant now = Instant.now();
        installations.forEach(installation -> installation.setRemovedAt(now));
        installationRepository.saveAll(installations);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<UUID> installedEnvironmentOf(UUID sensorId) {
        return installationRepository.findBySensorIdAndRemovedAtIsNull(sensorId)
                .map(SensorInstallation::getEducationalEnvironmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<UUID> activeInstallationId(UUID environmentId, UUID variableId) {
        return installationRepository.findAllByEducationalEnvironmentIdAndRemovedAtIsNull(environmentId)
                .stream()
                .filter(installation -> variableRepository.findBySensorId(installation.getSensorId()).stream()
                        .anyMatch(binding -> binding.getVariableId().equals(variableId)))
                .findFirst()
                .map(SensorInstallation::getId);
    }
}
