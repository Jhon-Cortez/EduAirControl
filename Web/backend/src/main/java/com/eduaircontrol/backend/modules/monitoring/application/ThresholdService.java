package com.eduaircontrol.backend.modules.monitoring.application;

import com.eduaircontrol.backend.modules.monitoring.entity.Severity;
import com.eduaircontrol.backend.modules.monitoring.entity.VariableThreshold;
import com.eduaircontrol.backend.modules.monitoring.repository.SeverityRepository;
import com.eduaircontrol.backend.modules.monitoring.repository.VariableThresholdRepository;
import com.eduaircontrol.backend.shared.contract.EnvironmentLookupPort;
import com.eduaircontrol.backend.shared.contract.ThresholdPort;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ThresholdService implements ThresholdPort {

    private final VariableThresholdRepository thresholdRepository;
    private final SeverityRepository severityRepository;
    private final EnvironmentLookupPort environmentLookupPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<Range> warningRange(UUID environmentId, UUID variableId) {
        EnvironmentLookupPort.EnvironmentInfo environment = requireEnvironment(environmentId);
        UUID warningId = severityId("WARNING");
        return thresholdRepository
                .findEffective(environmentId, environment.environmentTypeId(), variableId).stream()
                .filter(threshold -> warningId.equals(threshold.getSeverityId()))
                .findFirst()
                .map(threshold -> new Range(environmentId, variableId,
                        threshold.getMinValue(), threshold.getMaxValue()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Range> warningRanges(UUID environmentId) {
        EnvironmentLookupPort.EnvironmentInfo environment = requireEnvironment(environmentId);
        UUID warningId = severityId("WARNING");
        return thresholdRepository
                .findEffectiveBySeverity(environmentId, environment.environmentTypeId(), warningId).stream()
                .filter(threshold -> warningId.equals(threshold.getSeverityId()))
                .sorted(Comparator.comparing(VariableThreshold::getVariableId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(threshold -> new Range(environmentId, threshold.getVariableId(),
                        threshold.getMinValue(), threshold.getMaxValue()))
                .toList();
    }

    @Override
    @Transactional
    public void saveWarningRange(UUID environmentId, UUID environmentTypeId, UUID variableId,
                                 BigDecimal min, BigDecimal max) {
        VariableThreshold existing = thresholdRepository
                .findByEducationalEnvironmentIdAndVariableIdAndValidToIsNull(environmentId, variableId)
                .orElse(null);
        if (existing != null) {
            existing.setMinValue(min);
            existing.setMaxValue(max);
            thresholdRepository.save(existing);
            return;
        }
        thresholdRepository.save(VariableThreshold.builder()
                .id(UUID.randomUUID())
                .variableId(variableId)
                .environmentTypeId(environmentTypeId)
                .educationalEnvironmentId(environmentId)
                .minValue(min)
                .maxValue(max)
                .severityId(severityId("WARNING"))
                .validFrom(LocalDate.now().minusDays(1))
                .build());
    }

    @Override
    @Transactional
    public void deleteByEnvironment(UUID environmentId) {
        thresholdRepository.deleteByEducationalEnvironment(environmentId);
    }

    private UUID severityId(String code) {
        Severity severity = severityRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Severidad no sembrada: " + code));
        return severity.getId();
    }

    private EnvironmentLookupPort.EnvironmentInfo requireEnvironment(UUID id) {
        return environmentLookupPort.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ambiente no encontrado"));
    }
}
