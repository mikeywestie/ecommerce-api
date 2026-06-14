package com.mikey.ecommerce.cart;

import com.mikey.ecommerce.security.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

  Optional<Cart> findByUser(AppUser user);
}
