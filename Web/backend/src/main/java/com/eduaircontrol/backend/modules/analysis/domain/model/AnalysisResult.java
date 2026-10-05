package com.eduaircontrol.backend.modules.analysis.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resultado agregado de un analisis para una variable. El periodo analizado vive
 * en {@link EnvironmentalAnalysis}.
 */
@Entity
@Table(name = "analysis_result", schema = "monitoring")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "analysis_result_id", updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "environmental_analysis_id", nullable = false)
    private EnvironmentalAnalysis environmentalAnalysis;

    @Column(name = "variable_id", nullable = false)
    private UUID variableId;

    @Column(name = "min_value", nullable = false, precision = 12, scale = 4)
    private BigDecimal minValue;

    @Column(name = "max_value", nullable = false, precision = 12, scale = 4)
    private BigDecimal maxValue;

    @Column(name = "avg_value", nullable = false, precision = 12, scale = 4)
    private BigDecimal avgValue;

    @Column(name = "sample_count", nullable = false)
    private Integer sampleCount;

    @Column(name = "exceedance_count", nullable = false)
    private Integer exceedanceCount;
}