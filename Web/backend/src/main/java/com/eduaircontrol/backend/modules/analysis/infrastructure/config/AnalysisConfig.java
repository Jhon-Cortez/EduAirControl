package com.eduaircontrol.backend.modules.analysis.infrastructure.config;

import com.eduaircontrol.backend.modules.analysis.domain.service.PeriodResolver;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalysisConfig {

    /**
     * El reloj se inyecta para que los casos de uso sean deterministas en las pruebas.
     */
    @Bean
    public Clock analysisClock() {
        return Clock.systemUTC();
    }

    @Bean
    public PeriodResolver periodResolver(Clock analysisClock) {
        return new PeriodResolver(analysisClock);
    }
}