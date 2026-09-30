package com.eduaircontrol.backend.modules.device.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispositivo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dispositivo")
    private Long id;

    @Column(name = "mac_address", nullable = false, unique = true, length = 17)
    private String macAddress;

    @Column(length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String tipo = "esp32";

    @Column(name = "id_aula")
    private Long idAula;

    @Column(length = 32)
    private String ssid;

    @Column(nullable = false, length = 20)
    private String estado = "pendiente";

    @Column(name = "firmware_version", length = 20)
    private String firmwareVersion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
