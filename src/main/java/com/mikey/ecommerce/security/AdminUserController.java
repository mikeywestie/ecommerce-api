package com.mikey.ecommerce.security;

import com.mikey.ecommerce.security.dto.AuthResponse;
import com.mikey.ecommerce.security.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AuthService authService;

    public AdminUserController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public AuthResponse createAdmin(@Valid @RequestBody RegisterRequest request) {
        return authService.registerAdmin(request);
    }
}