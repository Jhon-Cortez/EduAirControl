package com.eduaircontrol.backend.modules.ux.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "user_preferences", schema = "ux")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {

    @Id
    @Column(name = "preference_id")
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(length = 10)
    private String language;

    @Column(name = "color_theme", length = 20)
    private String colorTheme;

    @Column(name = "notifications_enabled", nullable = false)
    private boolean notificationsEnabled;

    @Column(name = "time_zone", length = 100)
    private String timeZone;

    @Column(name = "date_format", length = 20)
    private String dateFormat;

    /** Recordatorios del SettingsScreen: {"alertas":true,...}. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reminders", nullable = false, columnDefinition = "jsonb")
    private java.util.Map<String, Boolean> reminders;

    @Column(name = "dark_mode", nullable = false)
    private boolean darkMode;

    @Column(name = "auto_time_zone", nullable = false)
    private boolean autoTimeZone;
}
