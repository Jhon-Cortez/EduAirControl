package com.eduaircontrol.backend.modules.device;

import com.eduaircontrol.backend.modules.device.dto.request.DeviceRequest;
import com.eduaircontrol.backend.modules.device.entity.Device;
import com.eduaircontrol.backend.modules.device.repository.DeviceRepository;
import com.eduaircontrol.backend.modules.auth.entity.Role;
import com.eduaircontrol.backend.modules.auth.entity.Users;
import com.eduaircontrol.backend.modules.security.JwtService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void initToken() {
        adminToken = jwtService.generateToken(Users.builder()
                .name("Admin Tester")
                .email("admin@test.com")
                .password("x")
                .companyCode("EDU-2024")
                .role(Role.ADMIN)
                .build());
        userToken = jwtService.generateToken(Users.builder()
                .name("Tester")
                .email("tester@test.com")
                .password("x")
                .companyCode("EDU-2024")
                .role(Role.USER)
                .build());
    }

    @AfterEach
    void cleanUp() {
        deviceRepository.deleteAll();
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + adminToken);
    }

    private MockHttpServletRequestBuilder withUserAuth(MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + userToken);
    }

    private DeviceRequest createValidRequest() {
        DeviceRequest request = new DeviceRequest();
        request.setMacAddress("AA:BB:CC:DD:EE:FF");
        request.setNombre("ESP32 Salón 209");
        request.setTipo("esp32");
        request.setIdAula(1L);
        request.setSsid("EDU-Campus");
        request.setEstado("pendiente");
        request.setFirmwareVersion("1.0.0");
        return request;
    }

    private Device saveDevice(String mac, String estado) {
        return deviceRepository.save(Device.builder()
                .macAddress(mac)
                .nombre("Dispositivo")
                .tipo("esp32")
                .estado(estado)
                .build());
    }

    @Test
    @Order(0)
    void deberiaRechazarSinToken() throws Exception {
        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(0)
    void deberiaPermitirLeerDispositivosAUsuarioNoAdmin() throws Exception {
        mockMvc.perform(withUserAuth(get("/api/devices")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(0)
    void deberiaRechazarCreacionDispositivoAUsuarioNoAdmin() throws Exception {
        mockMvc.perform(withUserAuth(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidRequest()))))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(0)
    void deberiaRechazarActualizacionDispositivoAUsuarioNoAdmin() throws Exception {
        Device device = saveDevice("AA:BB:CC:DD:EE:01", "pendiente");

        mockMvc.perform(withUserAuth(put("/api/devices/{id}", device.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidRequest()))))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(0)
    void deberiaRechazarEliminacionDispositivoAUsuarioNoAdmin() throws Exception {
        Device device = saveDevice("AA:BB:CC:DD:EE:02", "pendiente");

        mockMvc.perform(withUserAuth(delete("/api/devices/{id}", device.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(1)
    void deberiaCrearDispositivo() throws Exception {
        mockMvc.perform(withAuth(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidRequest()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.macAddress").value("AA:BB:CC:DD:EE:FF"))
                .andExpect(jsonPath("$.nombre").value("ESP32 Salón 209"))
                .andExpect(jsonPath("$.tipo").value("esp32"))
                .andExpect(jsonPath("$.estado").value("pendiente"));
    }

    @Test
    @Order(2)
    void deberiaNormalizarMacEnMinusculas() throws Exception {
        DeviceRequest request = createValidRequest();
        request.setMacAddress("aa:bb:cc:dd:ee:10");

        mockMvc.perform(withAuth(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.macAddress").value("AA:BB:CC:DD:EE:10"));
    }

    @Test
    @Order(3)
    void deberiaRechazarDispositivoConMacDuplicada() throws Exception {
        saveDevice("AA:BB:CC:DD:EE:FF", "pendiente");

        mockMvc.perform(withAuth(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidRequest()))))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(4)
    void deberiaRechazarRequestConCamposVacios() throws Exception {
        mockMvc.perform(withAuth(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DeviceRequest()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void deberiaRechazarMacInvalida() throws Exception {
        DeviceRequest request = createValidRequest();
        request.setMacAddress("no-es-una-mac");

        mockMvc.perform(withAuth(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    void deberiaRechazarEstadoInvalido() throws Exception {
        DeviceRequest request = createValidRequest();
        request.setEstado("volando");

        mockMvc.perform(withAuth(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void deberiaRechazarTipoInvalido() throws Exception {
        DeviceRequest request = createValidRequest();
        request.setTipo("arduino");

        mockMvc.perform(withAuth(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    void deberiaListarDispositivos() throws Exception {
        saveDevice("AA:BB:CC:DD:EE:01", "conectado");
        saveDevice("AA:BB:CC:DD:EE:02", "offline");

        mockMvc.perform(withAuth(get("/api/devices")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Order(9)
    void deberiaObtenerDispositivoPorId() throws Exception {
        Device device = saveDevice("AA:BB:CC:DD:EE:03", "conectado");

        mockMvc.perform(withAuth(get("/api/devices/{id}", device.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.macAddress").value("AA:BB:CC:DD:EE:03"))
                .andExpect(jsonPath("$.estado").value("conectado"));
    }

    @Test
    @Order(10)
    void deberiaRetornar404SiDispositivoNoExiste() throws Exception {
        mockMvc.perform(withAuth(get("/api/devices/{id}", 99999)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(11)
    void deberiaActualizarDispositivo() throws Exception {
        Device device = saveDevice("AA:BB:CC:DD:EE:04", "pendiente");

        DeviceRequest request = createValidRequest();
        request.setMacAddress("AA:BB:CC:DD:EE:04");
        request.setNombre("ESP32 Actualizado");
        request.setEstado("conectado");

        mockMvc.perform(withAuth(put("/api/devices/{id}", device.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("ESP32 Actualizado"))
                .andExpect(jsonPath("$.estado").value("conectado"));
    }

    @Test
    @Order(12)
    void deberiaEliminarDispositivo() throws Exception {
        Device device = saveDevice("AA:BB:CC:DD:EE:05", "pendiente");

        mockMvc.perform(withAuth(delete("/api/devices/{id}", device.getId())))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/api/devices/{id}", device.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(13)
    void deberiaRetornar404AlEliminarDispositivoInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/api/devices/{id}", 99999)))
                .andExpect(status().isNotFound());
    }
}
