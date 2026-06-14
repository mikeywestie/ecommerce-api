package com.mikey.ecommerce.mapper;

import com.mikey.ecommerce.dto.product.ProductResponse;
import com.mikey.ecommerce.dto.product.ProductSummaryResponse;
import com.mikey.ecommerce.product.Product;

public class ProductMapper {

  private ProductMapper() {}

  public static ProductResponse toResponse(Product product) {
    return toResponse(product, null);
  }

  public static ProductResponse toResponse(Product product, Integer availableQuantity) {
    String stockStatus = resolveStockStatus(product, availableQuantity);
    String stockMessage = resolveStockMessage(product, availableQuantity, stockStatus);

    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getCategory(),
        product.getSubcategory(),
        product.getBrand(),
        product.getImageUrl(),
        product.isActive(),
        product.getPrice(),
        availableQuantity,
        stockStatus,
        stockMessage,
        product.getCreatedAt());
  }

  public static ProductSummaryResponse toSummary(Product product) {
    return new ProductSummaryResponse(
        product.getId(),
        product.getName(),
        product.getCategory(),
        product.getSubcategory(),
        product.getBrand(),
        product.getImageUrl(),
        product.isActive(),
        product.getPrice());
  }

  private static String resolveStockStatus(Product product, Integer availableQuantity) {
    if (!product.isActive()) {
      return "INACTIVE";
    }

    if (availableQuantity == null) {
      return null;
    }

    if (availableQuantity <= 0) {
      return "OUT_OF_STOCK";
    }

    if (availableQuantity <= 5) {
      return "LOW_STOCK";
    }

    if (availableQuantity <= 10) {
      return "ALMOST_SOLD_OUT";
    }

    return "IN_STOCK";
  }

  private static String resolveStockMessage(
      Product product, Integer availableQuantity, String stockStatus) {
    if (stockStatus == null) {
      return null;
    }

    return switch (stockStatus) {
      case "INACTIVE" -> "This product is no longer available.";
      case "OUT_OF_STOCK" -> "This product is currently out of stock.";
      case "LOW_STOCK" -> "Low stock. " + availableQuantity + " left.";
      case "ALMOST_SOLD_OUT" -> "Almost sold out. " + availableQuantity + " left.";
      default -> availableQuantity + " available.";
    };
  }
}
