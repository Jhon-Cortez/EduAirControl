-- Indices recomendados por los modelos de 06-data/domain/*.

-- classrooms
CREATE INDEX idx_educational_environment_campus
    ON classrooms.educational_environment (campus_id);
CREATE INDEX idx_educational_environment_type
    ON classrooms.educational_environment (environment_type_id);
CREATE INDEX idx_educational_environment_status
    ON classrooms.educational_environment (status);

-- sensors
CREATE INDEX idx_sensor_installation_sensor
    ON sensors.sensor_installation (sensor_id, installed_at);
CREATE INDEX idx_sensor_installation_environment
    ON sensors.sensor_installation (educational_environment_id, installed_at);

-- monitoring
CREATE INDEX idx_environment_measurement_installation_variable_time
    ON monitoring.environment_measurement (sensor_installation_id, variable_id, measured_at);
CREATE INDEX idx_environment_measurement_variable_time
    ON monitoring.environment_measurement (variable_id, measured_at);
CREATE INDEX idx_environmental_analysis_environment
    ON monitoring.environmental_analysis (educational_environment_id, period_start);
CREATE INDEX idx_environmental_analysis_period
    ON monitoring.environmental_analysis (period_start, period_end);
CREATE INDEX idx_environment_alert_environment
    ON monitoring.environment_alert (educational_environment_id);
CREATE INDEX idx_environment_alert_variable
    ON monitoring.environment_alert (variable_id);
CREATE INDEX idx_environment_alert_threshold
    ON monitoring.environment_alert (variable_threshold_id);
CREATE INDEX idx_environment_alert_measurement
    ON monitoring.environment_alert (triggering_measurement_id);
CREATE INDEX idx_environment_alert_status
    ON monitoring.environment_alert (alert_status_id);
CREATE INDEX idx_environment_alert_raised_at
    ON monitoring.environment_alert (raised_at);
CREATE INDEX idx_environment_alert_deleted
    ON monitoring.environment_alert (deleted_at)
    WHERE deleted_at IS NULL;

-- ux (user_preferences ya queda indexada por su UNIQUE (user_id);
-- analysis_result ya queda indexada por su UNIQUE (environmental_analysis_id, variable_id))
CREATE INDEX idx_favorites_user_id
    ON ux.favorites (user_id);
CREATE INDEX idx_favorites_classroom_id
    ON ux.favorites (classroom_id);
CREATE INDEX idx_favorites_variable_id
    ON ux.favorites (variable_id);
CREATE INDEX idx_classroom_ratings_user_id
    ON ux.classroom_ratings (user_id);
CREATE INDEX idx_classroom_ratings_classroom_id
    ON ux.classroom_ratings (classroom_id);
CREATE INDEX idx_searches_user_id
    ON ux.searches (user_id);
CREATE INDEX idx_searches_searched_at
    ON ux.searches (searched_at);
