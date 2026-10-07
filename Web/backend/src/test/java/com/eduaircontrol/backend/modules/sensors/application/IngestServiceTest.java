package com.eduaircontrol.backend.modules.sensors.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eduaircontrol.backend.modules.sensors.dto.IngestRequest;
import com.eduaircontrol.backend.modules.sensors.entity.Device;
import com.eduaircontrol.backend.modules.sensors.repository.DeviceRepository;
import com.eduaircontrol.backend.modules.sensors.repository.SensorRepository;
import com.eduaircontrol.backend.shared.contract.InstallationPort;
import com.eduaircontrol.backend.shared.contract.MeasurementWritePort;
import com.eduaircontrol.backend.shared.contract.VariableCatalogPort;
import com.eduaircontrol.backend.shared.outbox.EnvironmentalDataRecorded;
import com.eduaircontrol.backend.shared.outbox.OutboxWriter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Verifica que la ingesta aceptada se convierte al sobre canonico de dominio.
 *
 * <p>El sobre representa una instantanea, no las lecturas sueltas. Por eso las
 * aceptaciones con instantes distintos generan un hecho por instante y conservan su
 * marca temporal en {@code occurredAt}.
 */
@ExtendWith(MockitoExtension.class)
class IngestServiceTest {

    private static final UUID ENVIRONMENT_ID = UUID.fromString("11111111-1111-4111-8111-111111111111");

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private VariableCatalogPort variableCatalogPort;

    @Mock
    private InstallationPort installationPort;

    @Mock
    private MeasurementWritePort measurementWritePort;

    @Mock
    private OutboxWriter outboxWriter;

    private IngestService service;

    @BeforeEach
    void setUp() {
        Device device = Device.builder()
                .id(UUID.randomUUID())
                .educationalEnvironmentId(ENVIRONMENT_ID)
                .status("desconectado")
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(device, null));
        service = new IngestService(
                deviceRepository,
                sensorRepository,
                variableCatalogPort,
                installationPort,
                measurementWritePort,
                outboxWriter);
        ReflectionTestUtils.setField(service, "outboxEnabled", true);
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void emitsOneCanonicalEventPerAcceptedInstant() {
        Instant first = Instant.parse("2026-10-07T12:00:00Z");
        Instant second = first.plusSeconds(3_600);
        stubVariable("temperature", "21.5");
        stubVariable("humidity", "55.0");
        stubVariable("co2", "612");
        stubVariable("noise", "48.0");
        when(installationPort.activeInstallationId(eq(ENVIRONMENT_ID), any(UUID.class)))
                .thenAnswer(invocation -> Optional.of(UUID.randomUUID()));
        when(measurementWritePort.record(any(UUID.class), any(UUID.class), any(BigDecimal.class),
                any(Instant.class))).thenReturn(true);
        when(deviceRepository.save(any(Device.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        IngestRequest request = new IngestRequest();
        request.setReadings(List.of(
                reading("temperature", "21.5", first),
                reading("humidity", "55.0", first),
                reading("co2", "612", second),
                reading("noise", "48.0", second)));

        IngestService.IngestResponse response = service.ingest(request);

        assertThat(response.accepted()).isEqualTo(4);
        ArgumentCaptor<Object> bodies = ArgumentCaptor.forClass(Object.class);
        verify(outboxWriter, times(2)).append(
                eq(EnvironmentalDataRecorded.TYPE),
                eq(EnvironmentalDataRecorded.AGGREGATE_TYPE),
                eq(ENVIRONMENT_ID.toString()),
                eq(EnvironmentalDataRecorded.ROUTING_KEY),
                bodies.capture(),
                any(Instant.class));
        List<EnvironmentalDataRecorded> events = bodies.getAllValues().stream()
                .map(EnvironmentalDataRecorded.class::cast)
                .toList();

        EnvironmentalDataRecorded firstEvent = events.stream()
                .filter(event -> event.occurredAt().equals(first))
                .findFirst()
                .orElseThrow();
        assertThat(firstEvent.payload().temperature()).isEqualByComparingTo("21.5");
        assertThat(firstEvent.payload().humidity()).isEqualByComparingTo("55.0");
        assertThat(firstEvent.payload().co2()).isNull();
        assertThat(firstEvent.payload().noiseLevel()).isNull();

        EnvironmentalDataRecorded secondEvent = events.stream()
                .filter(event -> event.occurredAt().equals(second))
                .findFirst()
                .orElseThrow();
        assertThat(secondEvent.payload().co2()).isEqualByComparingTo("612");
        assertThat(secondEvent.payload().noiseLevel()).isEqualByComparingTo("48.0");
        assertThat(secondEvent.metadata().correlationId())
                .isEqualTo(firstEvent.metadata().correlationId());
    }

    private void stubVariable(String code, String ignoredValue) {
        when(variableCatalogPort.findByCode(code)).thenReturn(Optional.of(
                new VariableCatalogPort.VariableRef(UUID.randomUUID(), code, code)));
    }

    private IngestRequest.Reading reading(String variable, String value, Instant measuredAt) {
        IngestRequest.Reading reading = new IngestRequest.Reading();
        reading.setVariable(variable);
        reading.setValue(new BigDecimal(value));
        reading.setMeasuredAt(measuredAt);
        return reading;
    }
}
