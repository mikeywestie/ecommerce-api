package com.mikey.ecommerce.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.security.dto.AuthResponse;
import com.mikey.ecommerce.security.dto.LoginRequest;
import com.mikey.ecommerce.security.dto.RegisterRequest;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private AppUserRepository appUserRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtService jwtService;

  private AuthService authService;

  @BeforeEach
  void setUp() {
    authService = new AuthService(appUserRepository, passwordEncoder, jwtService);
  }

  @Test
  void register_shouldDefaultToCustomerWhenRoleIsNotProvided() {
    RegisterRequest request =
        new RegisterRequest("Michael Westman", "michael@example.com", "password123", null);

    when(appUserRepository.existsByEmail("michael@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
    when(appUserRepository.save(any(AppUser.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(jwtService.generateToken(any(AppUser.class))).thenReturn("customer-token");

    AuthResponse response = authService.register(request);

    assertThat(response.token()).isEqualTo("customer-token");
    assertThat(response.tokenType()).isEqualTo("Bearer");
    assertThat(response.email()).isEqualTo("michael@example.com");
    assertThat(response.role()).isEqualTo("CUSTOMER");

    ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
    verify(appUserRepository).save(userCaptor.capture());

    assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.CUSTOMER);
  }

  @Test
  void register_shouldRegisterCustomerWhenCustomerRoleIsProvided() {
    RegisterRequest request =
        new RegisterRequest("Customer User", "customer@example.com", "password123", Role.CUSTOMER);

    when(appUserRepository.existsByEmail("customer@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
    when(appUserRepository.save(any(AppUser.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(jwtService.generateToken(any(AppUser.class))).thenReturn("customer-token");

    AuthResponse response = authService.register(request);

    assertThat(response.email()).isEqualTo("customer@example.com");
    assertThat(response.role()).isEqualTo("CUSTOMER");

    ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
    verify(appUserRepository).save(userCaptor.capture());

    assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.CUSTOMER);
  }

  @Test
  void register_shouldAllowFirstAdminWhenNoAdminExists() {
    RegisterRequest request =
        new RegisterRequest("Admin User", "admin@example.com", "password123", Role.ADMIN);

    when(appUserRepository.existsByEmail("admin@example.com")).thenReturn(false);
    when(appUserRepository.existsByRole(Role.ADMIN)).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
    when(appUserRepository.save(any(AppUser.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(jwtService.generateToken(any(AppUser.class))).thenReturn("admin-token");

    AuthResponse response = authService.register(request);

    assertThat(response.token()).isEqualTo("admin-token");
    assertThat(response.tokenType()).isEqualTo("Bearer");
    assertThat(response.email()).isEqualTo("admin@example.com");
    assertThat(response.role()).isEqualTo("ADMIN");

    ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
    verify(appUserRepository).save(userCaptor.capture());

    assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.ADMIN);
  }

  @Test
  void register_shouldBlockAdminRegistrationWhenAdminAlreadyExists() {
    RegisterRequest request =
        new RegisterRequest("Second Admin", "second.admin@example.com", "password123", Role.ADMIN);

    when(appUserRepository.existsByEmail("second.admin@example.com")).thenReturn(false);
    when(appUserRepository.existsByRole(Role.ADMIN)).thenReturn(true);

    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(ApiException.class)
        .hasMessage("Only an admin can register another admin");

    verify(passwordEncoder, never()).encode(any());
    verify(appUserRepository, never()).save(any());
    verify(jwtService, never()).generateToken(any());
  }

  @Test
  void register_shouldThrowApiExceptionWhenEmailAlreadyExists() {
    RegisterRequest request =
        new RegisterRequest("Michael Westman", "michael@example.com", "password123", Role.CUSTOMER);

    when(appUserRepository.existsByEmail("michael@example.com")).thenReturn(true);

    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(ApiException.class)
        .hasMessage("Email already registered");

    verify(passwordEncoder, never()).encode(any());
    verify(appUserRepository, never()).save(any());
    verify(jwtService, never()).generateToken(any());
  }

  @Test
  void login_shouldReturnAuthResponseWhenCredentialsAreValid() {
    AppUser user =
        new AppUser("Michael Westman", "michael@example.com", "encoded-password", Role.CUSTOMER);

    LoginRequest request = new LoginRequest("michael@example.com", "password123");

    when(appUserRepository.findByEmail("michael@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
    when(jwtService.generateToken(user)).thenReturn("login-token");

    AuthResponse response = authService.login(request);

    assertThat(response.token()).isEqualTo("login-token");
    assertThat(response.tokenType()).isEqualTo("Bearer");
    assertThat(response.email()).isEqualTo("michael@example.com");
    assertThat(response.role()).isEqualTo("CUSTOMER");
  }

  @Test
  void login_shouldThrowApiExceptionWhenEmailDoesNotExist() {
    LoginRequest request = new LoginRequest("missing@example.com", "password123");

    when(appUserRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(ApiException.class)
        .hasMessage("Invalid email or password");

    verify(passwordEncoder, never()).matches(any(), any());
    verify(jwtService, never()).generateToken(any());
  }

  @Test
  void login_shouldThrowApiExceptionWhenPasswordIsInvalid() {
    AppUser user =
        new AppUser("Michael Westman", "michael@example.com", "encoded-password", Role.CUSTOMER);

    LoginRequest request = new LoginRequest("michael@example.com", "wrong-password");

    when(appUserRepository.findByEmail("michael@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(ApiException.class)
        .hasMessage("Invalid email or password");

    verify(jwtService, never()).generateToken(any());
  }
}
