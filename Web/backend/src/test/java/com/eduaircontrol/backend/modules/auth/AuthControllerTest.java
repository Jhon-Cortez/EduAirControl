package com.eduaircontrol.backend.modules.auth;

import com.eduaircontrol.backend.modules.auth.dto.request.LoginRequest;
import com.eduaircontrol.backend.modules.auth.dto.request.RegisterRequest;
import com.eduaircontrol.backend.modules.auth.entity.PasswordResetToken;
import com.eduaircontrol.backend.modules.auth.entity.Role;
import com.eduaircontrol.backend.modules.auth.entity.Users;
import com.eduaircontrol.backend.modules.auth.repository.PasswordResetTokenRepository;
import com.eduaircontrol.backend.modules.auth.repository.UserRepository;
import com.eduaircontrol.backend.modules.auth.service.implement.EmailService;
import com.eduaircontrol.backend.modules.security.JwtService;
import java.time.Instant;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private EmailService emailService;

    private String resetEmail;

    @BeforeEach
    void setUp() {
        resetEmail = "reset-user@test.com";
        doNothing().when(emailService).sendPasswordResetCode(anyString(), anyString());
    }

    @AfterEach
    void cleanUp() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    private RegisterRequest validRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Usuario Test");
        request.setEmail("auth-user@test.com");
        request.setPassword("Password1");
        request.setCompanyCode("EDU-2024");
        return request;
    }

    private LoginRequest validLogin() {
        LoginRequest request = new LoginRequest();
        request.setEmail("auth-user@test.com");
        request.setPassword("Password1");
        request.setCompanyCode("EDU-2024");
        return request;
    }

    private void createUser(String email, String rawPassword) {
        userRepository.save(Users.builder()
                .name("Reset User")
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .companyCode("EDU-2024")
                .role(Role.USER)
                .build());
    }

    @Test
    void deberiaRegistrarYDevolverToken() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegister())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void deberiaRechazarRegistroDuplicado() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegister())))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegister())))
                .andExpect(status().isConflict());
    }

    @Test
    void deberiaRechazarPasswordCortaEnRegistro() throws Exception {
        RegisterRequest request = validRegister();
        request.setPassword("short");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaLoginCorrecto() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegister())))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLogin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void deberiaRechazarLoginPasswordIncorrecta() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegister())))
                .andExpect(status().isOk());

        LoginRequest request = validLogin();
        request.setPassword("WrongPass1");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deberiaRechazarLoginCompanyCodeIncorrecto() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegister())))
                .andExpect(status().isOk());

        LoginRequest request = validLogin();
        request.setCompanyCode("OTR-9999");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void forgotPasswordSiempreDevuelve200() throws Exception {
        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"noexiste@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());

        createUser(resetEmail, "Password1");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + resetEmail + "\"}"))
                .andExpect(status().isOk());

        verify(emailService).sendPasswordResetCode(anyString(), anyString());
    }

    @Test
    void flujoCompletoDeRecuperacion() throws Exception {
        createUser(resetEmail, "Password1");

        String knownCode = "123456";
        tokenRepository.deleteByEmail(resetEmail);
        tokenRepository.save(PasswordResetToken.builder()
                .email(resetEmail)
                .codeHash(passwordEncoder.encode(knownCode))
                .expiresAt(Instant.now().plusSeconds(900))
                .used(false)
                .attempts(0)
                .createdAt(Instant.now())
                .build());

        mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + resetEmail + "\",\"code\":\"" + knownCode + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","code":"%s","newPassword":"NuevaPassword1"}
                                """.formatted(resetEmail, knownCode)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"NuevaPassword1","companyCode":"EDU-2024"}
                                """.formatted(resetEmail)))
                .andExpect(status().isOk());
    }

    @Test
    void verifyCodeRechazaCodigoIncorrecto() throws Exception {
        createUser(resetEmail, "Password1");
        tokenRepository.deleteByEmail(resetEmail);
        tokenRepository.save(PasswordResetToken.builder()
                .email(resetEmail)
                .codeHash(passwordEncoder.encode("123456"))
                .expiresAt(Instant.now().plusSeconds(900))
                .used(false)
                .attempts(0)
                .createdAt(Instant.now())
                .build());

        mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + resetEmail + "\",\"code\":\"000000\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePasswordRequiereSesion() throws Exception {
        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"Password1\",\"newPassword\":\"NuevaPassword1\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePasswordFuncionaConToken() throws Exception {
        createUser(resetEmail, "Password1");
        String token = jwtService.generateToken(userRepository.findByEmail(resetEmail).orElseThrow());

        mockMvc.perform(post("/auth/change-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"Password1\",\"newPassword\":\"NuevaPassword1\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"NuevaPassword1","companyCode":"EDU-2024"}
                                """.formatted(resetEmail)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAccountFuncionaConPasswordCorrecta() throws Exception {
        createUser(resetEmail, "Password1");
        String token = jwtService.generateToken(userRepository.findByEmail(resetEmail).orElseThrow());

        mockMvc.perform(delete("/auth/account")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"Password1\"}"))
                .andExpect(status().isOk());

        org.junit.jupiter.api.Assertions.assertTrue(
                userRepository.findByEmail(resetEmail).isEmpty());
    }

    @Test
    void deleteAccountRechazaPasswordIncorrecta() throws Exception {
        createUser(resetEmail, "Password1");
        String token = jwtService.generateToken(userRepository.findByEmail(resetEmail).orElseThrow());

        mockMvc.perform(delete("/auth/account")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"WrongPass1\"}"))
                .andExpect(status().isUnauthorized());
    }
}
