package com.mikey.ecommerce.mapper;

import com.mikey.ecommerce.dto.product.ProductResponse;
import com.mikey.ecommerce.dto.product.ProductSummaryResponse;
import com.mikey.ecommerce.product.Product;

public class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product p){
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getCreatedAt()
        );
    }

    public static ProductSummaryResponse toSummary(Product p){
        return new ProductSummaryResponse(
                p.getId(),
                p.getName(),
                p.getPrice()
        );
    }
}