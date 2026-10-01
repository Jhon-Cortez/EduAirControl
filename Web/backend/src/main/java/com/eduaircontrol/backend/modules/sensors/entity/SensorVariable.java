package com.eduaircontrol.backend.modules.sensors.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sensor_variable", schema = "sensors")
@IdClass(SensorVariable.Pk.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SensorVariable {

    @Id
    @Column(name = "sensor_id")
    private UUID sensorId;

    @Id
    @Column(name = "variable_id")
    private UUID variableId;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Pk implements Serializable {
        private UUID sensorId;
        private UUID variableId;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Pk other)) {
                return false;
            }
            return Objects.equals(sensorId, other.sensorId) && Objects.equals(variableId, other.variableId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sensorId, variableId);
        }
    }
}
