package com.mikey.ecommerce.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p
            FROM Product p
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
              AND p.price BETWEEN :minPrice AND :maxPrice
              AND (:category = '' OR p.category = :category)
              AND (:subcategory = '' OR p.subcategory = :subcategory)
              AND (:brand = '' OR p.brand = :brand)
            """)
    Page<Product> searchProducts(
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String category,
            String subcategory,
            String brand,
            Pageable pageable
    );

    Page<Product> findByNameContainingIgnoreCaseAndPriceBetween(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );
}