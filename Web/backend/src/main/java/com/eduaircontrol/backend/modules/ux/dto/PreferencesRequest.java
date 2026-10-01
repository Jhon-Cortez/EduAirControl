package com.eduaircontrol.backend.modules.ux.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreferencesRequest {

    private String language;
    private String dateFormat;
    private Boolean autoTimezone;
    private String manualTimezone;
    private java.util.Map<String, Boolean> reminders;
    private Boolean darkMode;
    private String colorTheme;
    private Boolean notificationsEnabled;
}
