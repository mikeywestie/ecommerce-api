package com.mikey.ecommerce.product;

import com.mikey.ecommerce.inventory.Inventory;
import com.mikey.ecommerce.inventory.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DemoCatalogSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public DemoCatalogSeeder(
            ProductRepository productRepository,
            InventoryRepository inventoryRepository
    ) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<DemoProduct> demoProducts = List.of(
                new DemoProduct(
                        "Wireless Mechanical Keyboard",
                        "Compact RGB mechanical keyboard with hot-swappable switches.",
                        "Peripherals",
                        "https://images.unsplash.com/photo-1587829741301-dc798b83add3",
                        new BigDecimal("1299.99"),
                        18
                ),
                new DemoProduct(
                        "Ergonomic Gaming Mouse",
                        "High precision wireless mouse with programmable buttons.",
                        "Peripherals",
                        "https://images.unsplash.com/photo-1527814050087-3793815479db",
                        new BigDecimal("799.99"),
                        24
                ),
                new DemoProduct(
                        "USB-C Docking Station",
                        "Multi-port docking station for laptops and workstations.",
                        "Accessories",
                        "https://images.unsplash.com/photo-1625842268584-8f3296236761",
                        new BigDecimal("1499.99"),
                        12
                ),
                new DemoProduct(
                        "Noise Cancelling Headphones",
                        "Wireless over-ear headphones with active noise cancellation.",
                        "Audio",
                        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e",
                        new BigDecimal("2199.99"),
                        9
                ),
                new DemoProduct(
                        "4K UltraWide Monitor",
                        "Curved 34-inch ultra-wide monitor for productivity and gaming.",
                        "Displays",
                        "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf",
                        new BigDecimal("8999.99"),
                        6
                ),
                new DemoProduct(
                        "Portable Bluetooth Speaker",
                        "Compact waterproof speaker with rich bass and long battery life.",
                        "Audio",
                        "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1",
                        new BigDecimal("999.99"),
                        30
                ),
                new DemoProduct(
                        "Smart Fitness Watch",
                        "Fitness tracking watch with heart-rate monitoring and GPS.",
                        "Wearables",
                        "https://images.unsplash.com/photo-1523275335684-37898b6baf30",
                        new BigDecimal("2499.99"),
                        14
                ),
                new DemoProduct(
                        "Laptop Stand",
                        "Adjustable aluminium laptop stand for ergonomic desk setups.",
                        "Accessories",
                        "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46",
                        new BigDecimal("649.99"),
                        22
                )
        );

        for (DemoProduct demoProduct : demoProducts) {
            Product product = productRepository
                    .findAll()
                    .stream()
                    .filter(existing -> existing.getName().equalsIgnoreCase(demoProduct.name()))
                    .findFirst()
                    .orElseGet(() -> productRepository.save(
                            new Product(
                                    demoProduct.name(),
                                    demoProduct.description(),
                                    demoProduct.category(),
                                    demoProduct.imageUrl(),
                                    true,
                                    demoProduct.price()
                            )
                    ));

            inventoryRepository
                    .findByProductId(product.getId())
                    .orElseGet(() -> inventoryRepository.save(
                            new Inventory(product, demoProduct.quantityAvailable())
                    ));
        }
    }

    private record DemoProduct(
            String name,
            String description,
            String category,
            String imageUrl,
            BigDecimal price,
            int quantityAvailable
    ) {
    }
}