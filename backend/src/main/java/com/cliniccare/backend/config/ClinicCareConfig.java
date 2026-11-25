package com.cliniccare.backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.util.TimeZone;

@Configuration
public class ClinicCareConfig {
    @Value("${cliniccare.timezone:Asia/Kolkata}")
    private String timezone;

    @PostConstruct
    public void configureTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));
    }
}
