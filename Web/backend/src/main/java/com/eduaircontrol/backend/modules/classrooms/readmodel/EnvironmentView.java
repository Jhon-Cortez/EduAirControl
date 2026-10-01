package com.eduaircontrol.backend.modules.classrooms.readmodel;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Ambiente con sus metricas actuales: es el objeto que consume el frontend
 * (Dashboard, grid, detalle y panel de gestion).
 */
@Getter
@Builder
public class EnvironmentView {

    private final UUID id;
    private final String code;
    private final String name;
    private final String building;
    private final String location;
    private final UUID campusId;
    private final String envType;
    private final UUID envTypeId;
    private final Integer floor;
    private final Integer capacity;
    private final BigDecimal tempMin;
    private final BigDecimal tempMax;
    private final BigDecimal temp;
    private final BigDecimal humidity;
    private final BigDecimal co2;
    private final BigDecimal noise;
    private final String statusKey;
    private final boolean favorite;
    private final String lastUpdate;
}
