package com.mikey.ecommerce.mapper;

import com.mikey.ecommerce.dto.product.ProductResponse;
import com.mikey.ecommerce.dto.product.ProductSummaryResponse;
import com.mikey.ecommerce.product.Product;

public class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product p) {
        return toResponse(p, null);
    }

    public static ProductResponse toResponse(Product p, Integer availableQuantity) {
        int stock = availableQuantity == null ? 0 : availableQuantity;

        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getCategory(),
                p.getImageUrl(),
                p.isActive(),
                p.getPrice(),
                stock,
                resolveStockStatus(p, stock),
                resolveStockMessage(p, stock),
                p.getCreatedAt()
        );
    }

    public static ProductSummaryResponse toSummary(Product p) {
        return new ProductSummaryResponse(
                p.getId(),
                p.getName(),
                p.getCategory(),
                p.getImageUrl(),
                p.isActive(),
                p.getPrice()
        );
    }

    private static String resolveStockStatus(Product product, int stock) {
        if (!product.isActive()) {
            return "INACTIVE";
        }

        if (stock <= 0) {
            return "OUT_OF_STOCK";
        }

        if (stock <= 5) {
            return "LOW_STOCK";
        }

        if (stock <= 10) {
            return "ALMOST_SOLD_OUT";
        }

        return "IN_STOCK";
    }

    private static String resolveStockMessage(Product product, int stock) {
        if (!product.isActive()) {
            return "This product is no longer available.";
        }

        if (stock <= 0) {
            return "Out of stock.";
        }

        if (stock <= 5) {
            return "Low stock. " + stock + " left.";
        }

        if (stock <= 10) {
            return "Almost sold out. " + stock + " left.";
        }

        return stock + " available.";
    }
}