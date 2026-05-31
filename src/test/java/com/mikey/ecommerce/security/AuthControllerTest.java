package com.mikey.ecommerce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikey.ecommerce.security.dto.AuthResponse;
import com.mikey.ecommerce.security.dto.LoginRequest;
import com.mikey.ecommerce.security.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService))
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void register_shouldReturnAuthResponse() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Michael Westman",
                "michael@example.com",
                "password123"
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse(
                        "jwt-token",
                        "Bearer",
                        "michael@example.com",
                        "CUSTOMER"
                ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("michael@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void login_shouldReturnAuthResponse() throws Exception {
        LoginRequest request = new LoginRequest(
                "michael@example.com",
                "password123"
        );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse(
                        "jwt-token",
                        "Bearer",
                        "michael@example.com",
                        "CUSTOMER"
                ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("michael@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void register_shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Michael Westman",
                "not-an-email",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}