package com.eduaircontrol.backend.modules.monitoring.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "variable_threshold", schema = "monitoring")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariableThreshold {

    @Id
    @Column(name = "variable_threshold_id")
    private UUID id;

    @Column(name = "variable_id", nullable = false)
    private UUID variableId;

    @Column(name = "environment_type_id", nullable = false)
    private UUID environmentTypeId;

    @Column(name = "educational_environment_id")
    private UUID educationalEnvironmentId;

    @Column(name = "min_value")
    private BigDecimal minValue;

    @Column(name = "max_value")
    private BigDecimal maxValue;

    @Column(name = "severity_id", nullable = false)
    private UUID severityId;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;
}
