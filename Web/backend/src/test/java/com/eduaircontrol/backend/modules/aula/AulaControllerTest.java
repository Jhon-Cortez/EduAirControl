package com.eduaircontrol.backend.modules.aula;

import com.eduaircontrol.backend.modules.aula.dto.request.AulaRequest;
import com.eduaircontrol.backend.modules.aula.entity.Aula;
import com.eduaircontrol.backend.modules.aula.repository.AulaRepository;
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
class AulaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private String token;

    @BeforeEach
    void initToken() {
        Users user = Users.builder()
                .name("Tester")
                .email("tester@test.com")
                .password("x")
                .companyCode("EDU-2024")
                .role(Role.USER)
                .build();
        token = jwtService.generateToken(user);
    }

    @AfterEach
    void cleanUp() {
        aulaRepository.deleteAll();
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + token);
    }

    private AulaRequest createValidRequest() {
        AulaRequest request = new AulaRequest();
        request.setCodigoAula("AUL-001");
        request.setNombre("Sala de Prueba");
        request.setUbicacion("Bloque A - Piso 1");
        request.setCapacidad(30);
        request.setTipoAula("salon");
        request.setEstado("activa");
        return request;
    }

    @Test
    @Order(0)
    void deberiaRechazarSinToken() throws Exception {
        mockMvc.perform(get("/api/aulas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(1)
    void deberiaCrearAula() throws Exception {
        AulaRequest request = createValidRequest();

        mockMvc.perform(withAuth(post("/api/aulas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoAula").value("AUL-001"))
                .andExpect(jsonPath("$.nombre").value("Sala de Prueba"))
                .andExpect(jsonPath("$.tipoAula").value("salon"))
                .andExpect(jsonPath("$.estado").value("activa"));
    }

    @Test
    @Order(2)
    void deberiaRechazarAulaConCodigoDuplicado() throws Exception {
        AulaRequest request = createValidRequest();
        aulaRepository.save(Aula.builder()
                .codigoAula("AUL-001")
                .nombre("Existente")
                .tipoAula("salon")
                .estado("activa")
                .build());

        mockMvc.perform(withAuth(post("/api/aulas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    void deberiaRechazarRequestConCamposVacios() throws Exception {
        AulaRequest request = new AulaRequest();

        mockMvc.perform(withAuth(post("/api/aulas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    void deberiaRechazarTipoAulaInvalido() throws Exception {
        AulaRequest request = createValidRequest();
        request.setTipoAula("invalido");

        mockMvc.perform(withAuth(post("/api/aulas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void deberiaRechazarCapacidadCero() throws Exception {
        AulaRequest request = createValidRequest();
        request.setCapacidad(0);

        mockMvc.perform(withAuth(post("/api/aulas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    void deberiaListarAulas() throws Exception {
        aulaRepository.save(Aula.builder()
                .codigoAula("AUL-001")
                .nombre("Sala 1")
                .tipoAula("salon")
                .estado("activa")
                .build());
        aulaRepository.save(Aula.builder()
                .codigoAula("AUL-002")
                .nombre("Sala 2")
                .tipoAula("laboratorio")
                .estado("activa")
                .build());

        mockMvc.perform(withAuth(get("/api/aulas")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Order(7)
    void deberiaObtenerAulaPorId() throws Exception {
        Aula aula = aulaRepository.save(Aula.builder()
                .codigoAula("AUL-001")
                .nombre("Sala 1")
                .tipoAula("salon")
                .estado("activa")
                .build());

        mockMvc.perform(withAuth(get("/api/aulas/{id}", aula.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoAula").value("AUL-001"));
    }

    @Test
    @Order(8)
    void deberiaRetornar404SiAulaNoExiste() throws Exception {
        mockMvc.perform(withAuth(get("/api/aulas/{id}", 99999)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    void deberiaActualizarAula() throws Exception {
        Aula aula = aulaRepository.save(Aula.builder()
                .codigoAula("AUL-001")
                .nombre("Sala Original")
                .tipoAula("salon")
                .estado("activa")
                .build());

        AulaRequest request = createValidRequest();
        request.setNombre("Sala Actualizada");
        request.setCapacidad(40);

        mockMvc.perform(withAuth(put("/api/aulas/{id}", aula.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Sala Actualizada"))
                .andExpect(jsonPath("$.capacidad").value(40));
    }

    @Test
    @Order(10)
    void deberiaEliminarAula() throws Exception {
        Aula aula = aulaRepository.save(Aula.builder()
                .codigoAula("AUL-001")
                .nombre("Sala 1")
                .tipoAula("salon")
                .estado("activa")
                .build());

        mockMvc.perform(withAuth(delete("/api/aulas/{id}", aula.getId())))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/api/aulas/{id}", aula.getId())))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(11)
    void deberiaRetornar404AlEliminarAulaInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/api/aulas/{id}", 99999)))
                .andExpect(status().isNotFound());
    }
}
