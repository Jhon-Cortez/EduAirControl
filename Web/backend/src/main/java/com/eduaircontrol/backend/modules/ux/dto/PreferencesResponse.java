package com.eduaircontrol.backend.modules.ux.dto;

import com.eduaircontrol.backend.modules.ux.entity.UserPreference;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PreferencesResponse {

    private String language;
    private String dateFormat;
    private Boolean autoTimezone;
    private String manualTimezone;
    private java.util.Map<String, Boolean> reminders;
    private Boolean darkMode;
    private String colorTheme;
    private Boolean notificationsEnabled;

    public static PreferencesResponse from(UserPreference preference) {
        return PreferencesResponse.builder()
                .language(preference.getLanguage())
                .dateFormat(preference.getDateFormat())
                .autoTimezone(preference.isAutoTimeZone())
                .manualTimezone(preference.getTimeZone())
                .reminders(preference.getReminders())
                .darkMode(preference.isDarkMode())
                .colorTheme(preference.getColorTheme())
                .notificationsEnabled(preference.isNotificationsEnabled())
                .build();
    }
}
