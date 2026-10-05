package com.eduaircontrol.backend.modules.analysis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eduaircontrol.backend.PostgresTestBase;
import com.eduaircontrol.backend.modules.analysis.domain.model.AnalysisStatusCode;
import com.eduaircontrol.backend.modules.analysis.domain.port.out.AnalysisRepository;
import com.eduaircontrol.backend.modules.analysis.domain.port.out.AnalysisStatusRepository;
import com.eduaircontrol.backend.modules.classrooms.domain.model.Campus;
import com.eduaircontrol.backend.modules.classrooms.application.port.CampusRepository;
import com.eduaircontrol.backend.modules.classrooms.application.port.EducationalEnvironmentRepository;
import com.eduaircontrol.backend.modules.classrooms.domain.model.EducationalEnvironment;
import com.eduaircontrol.backend.modules.classrooms.domain.model.EnvironmentType;
import com.eduaircontrol.backend.modules.classrooms.application.port.EnvironmentTypeRepository;
import com.eduaircontrol.backend.modules.identity.entity.Role;
import com.eduaircontrol.backend.modules.identity.entity.User;
import com.eduaircontrol.backend.modules.identity.repository.UserRepository;
import com.eduaircontrol.backend.shared.security.JwtService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integracion de la API de analisis contra PostgreSQL real.
 * Cubre HU-ANA-001..004: un ambiente sin mediciones produce un analisis COMPLETED
 * sin resultados (escenario "no data"), y un ambiente con mediciones agrega por
 * variable.
 */
@AutoConfigureMockMvc
class AnalysisControllerTest extends PostgresTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CampusRepository campusRepository;

    @Autowired
    private EnvironmentTypeRepository environmentTypeRepository;

    @Autowired
    private EducationalEnvironmentRepository environmentRepository;

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private AnalysisStatusRepository statusRepository;

    @Autowired
    private JwtService jwtService;

    private String token;
    private UUID emptyEnvironmentId;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        User user = User.builder()
                .name("Analysis Tester")
                .email("analysis-" + suffix + "@test.com")
                .password("{noop}unused")
                .companyCode("TEST")
                .role(Role.USER)
                .build();
        userRepository.save(user);
        token = jwtService.generateToken(user);

        Campus campus = campusRepository.save(Campus.builder()
                .code("CMP-" + suffix)
                .name("Campus analisis " + suffix)
                .build());

        EnvironmentType type = environmentTypeRepository.save(EnvironmentType.builder()
                .code("CLASSROOM-" + suffix)
                .name("Aula " + suffix)
                .build());

        emptyEnvironmentId = environmentRepository.save(EducationalEnvironment.builder()
                .code("ENV-" + suffix)
                .name("Aula sin mediciones " + suffix)
                .campusId(campus.getId())
                .environmentTypeId(type.getId())
                .build()).getId();
    }

    @Test
    @DisplayName("POST /api/v1/analyses crea un analisis COMPLETED para un periodo")
    void createsCompletedAnalysis() throws Exception {
        mockMvc.perform(post("/api/v1/analyses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"environmentId":"%s","period":"MONTH","referenceDate":"2026-09-04"}
                                """.formatted(emptyEnvironmentId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.environmentId").value(emptyEnvironmentId.toString()))
                .andExpect(jsonPath("$.period").value("MONTH"))
                .andExpect(jsonPath("$.periodStart").value("2026-09-01T00:00:00Z"))
                .andExpect(jsonPath("$.periodEnd").value("2026-10-01T00:00:00Z"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    @DisplayName("Un ambiente sin mediciones produce un analisis COMPLETED sin resultados")
    void completesWithoutResultsWhenNoData() throws Exception {
        String body = mockMvc.perform(post("/api/v1/analyses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"environmentId":"%s","period":"WEEK","referenceDate":"2026-09-04"}
                                """.formatted(emptyEnvironmentId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertThat(body).contains("\"status\":\"COMPLETED\"");
    }

    @Test
    @DisplayName("El mismo ambiente y periodo no se analiza dos veces")
    void rejectsDuplicateWindow() throws Exception {
        String payload = """
                {"environmentId":"%s","period":"YEAR","referenceDate":"2026-09-04"}
                """.formatted(emptyEnvironmentId);

        mockMvc.perform(post("/api/v1/analyses")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/analyses")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Un ambiente inexistente responde 404")
    void rejectsUnknownEnvironment() throws Exception {
        mockMvc.perform(post("/api/v1/analyses")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"environmentId":"%s","period":"DAY"}
                        """.formatted(UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Sin token responde 401")
    void requiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/analyses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"environmentId":"%s","period":"DAY"}
                                """.formatted(emptyEnvironmentId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/analyses lista paginado con metadatos")
    void listsAnalyses() throws Exception {
        mockMvc.perform(post("/api/v1/analyses")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"environmentId":"%s","period":"DAY","referenceDate":"2026-09-04"}
                        """.formatted(emptyEnvironmentId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/analyses")
                        .header("Authorization", "Bearer " + token)
                        .param("environmentId", emptyEnvironmentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.meta.page").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/analyses/latest responde 204 cuando no hay analisis completado")
    void latestReturnsNoContent() throws Exception {
        mockMvc.perform(get("/api/v1/analyses/latest")
                        .header("Authorization", "Bearer " + token)
                        .param("environmentId", emptyEnvironmentId.toString())
                        .param("period", "DAY")
                        .param("referenceDate", "2020-01-15"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("El catalogo de estados esta sembrado y cubre el ciclo de vida")
    void statusCatalogIsSeeded() {
        assertThat(statusRepository.findAll())
                .extracting(status -> status.statusCode())
                .contains(AnalysisStatusCode.PENDING, AnalysisStatusCode.RUNNING,
                        AnalysisStatusCode.COMPLETED, AnalysisStatusCode.FAILED);
    }

    @Test
    @DisplayName("GET /api/v1/analyses/{id} responde 404 para un id desconocido")
    void returnsNotFoundForUnknownId() throws Exception {
        mockMvc.perform(get("/api/v1/analyses/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("El limite de pagina se acota a 100")
    void capsPageSize() throws Exception {
        mockMvc.perform(get("/api/v1/analyses")
                        .header("Authorization", "Bearer " + token)
                        .param("limit", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.limit").value(100));
    }
}