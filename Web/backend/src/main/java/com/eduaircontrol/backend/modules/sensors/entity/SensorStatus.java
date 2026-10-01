package com.eduaircontrol.backend.modules.sensors.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sensor_statuses", schema = "sensors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorStatus {

    @Id
    @Column(name = "sensor_status_id")
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false)
    private short level;
}
