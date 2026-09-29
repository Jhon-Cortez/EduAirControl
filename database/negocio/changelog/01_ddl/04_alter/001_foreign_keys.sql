-- Llaves foraneas intra-dominio (regla: FK fisica dentro del dominio,
-- referencia logica entre dominios — ADR-003 + 06-data/domain/*).

-- Dominio: classrooms
ALTER TABLE classrooms.educational_environment
    ADD CONSTRAINT fk_educational_environment_campus
    FOREIGN KEY (campus_id) REFERENCES classrooms.campuses (campus_id) ON DELETE RESTRICT,
    ADD CONSTRAINT fk_educational_environment_type
    FOREIGN KEY (environment_type_id) REFERENCES classrooms.environment_types (environment_type_id) ON DELETE RESTRICT;

-- Dominio: sensors
ALTER TABLE sensors.sensor_installation
    ADD CONSTRAINT fk_sensor_installation_sensor
    FOREIGN KEY (sensor_id) REFERENCES sensors.sensor (sensor_id) ON DELETE RESTRICT;

ALTER TABLE sensors.sensor_variable
    ADD CONSTRAINT fk_sensor_variable_sensor
    FOREIGN KEY (sensor_id) REFERENCES sensors.sensor (sensor_id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_sensor_variable_variable
    FOREIGN KEY (variable_id) REFERENCES sensors.variable (variable_id) ON DELETE RESTRICT;

-- Dominio: monitoring
ALTER TABLE monitoring.variable_threshold
    ADD CONSTRAINT fk_variable_threshold_severity
    FOREIGN KEY (severity_id) REFERENCES monitoring.severity (severity_id) ON DELETE RESTRICT;

ALTER TABLE monitoring.analysis_result
    ADD CONSTRAINT fk_analysis_result_environmental_analysis
    FOREIGN KEY (environmental_analysis_id) REFERENCES monitoring.environmental_analysis (environmental_analysis_id) ON DELETE CASCADE;

ALTER TABLE monitoring.environment_alert
    ADD CONSTRAINT fk_environment_alert_variable_threshold
    FOREIGN KEY (variable_threshold_id) REFERENCES monitoring.variable_threshold (variable_threshold_id) ON DELETE RESTRICT,
    ADD CONSTRAINT fk_environment_alert_triggering_measurement
    FOREIGN KEY (triggering_measurement_id) REFERENCES monitoring.environment_measurement (environment_measurement_id) ON DELETE RESTRICT;

-- Dominio: ux — sin FKs (todas sus referencias son logicas a otros servicios).
