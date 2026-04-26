package com.mikey.ecommerce.config;

import com.mikey.ecommerce.inventory.Inventory;
import com.mikey.ecommerce.inventory.InventoryRepository;
import com.mikey.ecommerce.product.Product;
import com.mikey.ecommerce.product.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(ProductRepository productRepository,
                               InventoryRepository inventoryRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                Product laptop = productRepository.save(
                        new Product("Java Developer Laptop", "Good laptop for Spring Boot development", new BigDecimal("12999.99"))
                );

                Product keyboard = productRepository.save(
                        new Product("Mechanical Keyboard", "RGB coding keyboard", new BigDecimal("899.99"))
                );

                Product mouse = productRepository.save(
                        new Product("Wireless Mouse", "Ergonomic mouse", new BigDecimal("399.99"))
                );

                inventoryRepository.save(new Inventory(laptop, 10));
                inventoryRepository.save(new Inventory(keyboard, 25));
                inventoryRepository.save(new Inventory(mouse, 40));
            }
        };
    }
}
