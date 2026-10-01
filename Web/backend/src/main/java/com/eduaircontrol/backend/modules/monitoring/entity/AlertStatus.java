package com.eduaircontrol.backend.modules.monitoring.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alert_statuses", schema = "monitoring")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertStatus {

    @Id
    @Column(name = "alert_status_id")
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 40)
    private String name;
}
