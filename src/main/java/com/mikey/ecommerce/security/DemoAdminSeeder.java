package com.mikey.ecommerce.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DemoAdminSeeder implements CommandLineRunner {

  private static final String DEMO_ADMIN_NAME = "Admin2";
  private static final String DEMO_ADMIN_EMAIL = "admin2@ecommerce.local";
  private static final String DEMO_ADMIN_PASSWORD = "Admin@12345";

  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;

  public DemoAdminSeeder(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
    this.appUserRepository = appUserRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(String... args) {
    AppUser demoAdmin =
        appUserRepository
            .findByEmail(DEMO_ADMIN_EMAIL)
            .orElseGet(
                () ->
                    new AppUser(
                        DEMO_ADMIN_NAME,
                        DEMO_ADMIN_EMAIL,
                        passwordEncoder.encode(DEMO_ADMIN_PASSWORD),
                        Role.ADMIN));

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
