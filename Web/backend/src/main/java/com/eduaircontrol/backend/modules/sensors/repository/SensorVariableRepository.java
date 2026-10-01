package com.eduaircontrol.backend.modules.sensors.repository;

import com.eduaircontrol.backend.modules.sensors.entity.SensorVariable;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorVariableRepository extends JpaRepository<SensorVariable, SensorVariable.Pk> {
    List<SensorVariable> findBySensorId(UUID sensorId);

    boolean existsBySensorIdAndVariableId(UUID sensorId, UUID variableId);

    void deleteBySensorId(UUID sensorId);
}
