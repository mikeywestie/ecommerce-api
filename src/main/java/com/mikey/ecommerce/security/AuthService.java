package com.mikey.ecommerce.security;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.security.dto.AuthResponse;
import com.mikey.ecommerce.security.dto.LoginRequest;
import com.mikey.ecommerce.security.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String DEMO_ADMIN_NAME = "Admin2";
    private static final String DEMO_ADMIN_EMAIL = "admin2@ecommerce.local";
    private static final String DEMO_ADMIN_PASSWORD = "Admin@12345";

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository appUserRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (appUserRepository.existsByEmail(request.email())) {
            throw new ApiException("Email already registered");
        }

        Role requestedRole = request.role() == null
                ? Role.CUSTOMER
                : request.role();

        if (requestedRole == Role.ADMIN && appUserRepository.existsByRole(Role.ADMIN)) {
            throw new ApiException("Only an admin can register another admin");
        }

        AppUser user = new AppUser(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                requestedRole
        );

        AppUser savedUser = appUserRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                token,
                "Bearer",
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        ensureDemoAdminExists();

        AppUser user = appUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ApiException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                "Bearer",
                user.getEmail(),
                user.getRole().name()
        );
    }

    public AuthResponse registerAdmin(RegisterRequest request) {
        if (appUserRepository.existsByEmail(request.email())) {
            throw new ApiException("Email already registered");
        }

        AppUser user = new AppUser(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.ADMIN
        );

        AppUser savedUser = appUserRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                token,
                "Bearer",
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    private void ensureDemoAdminExists() {
        AppUser demoAdmin = appUserRepository.findByEmail(DEMO_ADMIN_EMAIL)
                .orElseGet(() -> new AppUser(
                        DEMO_ADMIN_NAME,
                        DEMO_ADMIN_EMAIL,
                        passwordEncoder.encode(DEMO_ADMIN_PASSWORD),
                        Role.ADMIN
                ));

        boolean changed = false;

        if (!DEMO_ADMIN_NAME.equals(demoAdmin.getName())) {
            demoAdmin.setName(DEMO_ADMIN_NAME);
            changed = true;
        }

        if (demoAdmin.getRole() != Role.ADMIN) {
            demoAdmin.setRole(Role.ADMIN);
            changed = true;
        }

        if (!passwordEncoder.matches(DEMO_ADMIN_PASSWORD, demoAdmin.getPassword())) {
            demoAdmin.setPassword(passwordEncoder.encode(DEMO_ADMIN_PASSWORD));
            changed = true;
        }

        if (demoAdmin.getId() == null || changed) {
            appUserRepository.save(demoAdmin);
        }
    }
}