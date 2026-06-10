package com.mikey.ecommerce.product;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.dto.common.PageResponse;
import com.mikey.ecommerce.dto.product.ProductResponse;
import com.mikey.ecommerce.dto.product.ProductSummaryResponse;
import com.mikey.ecommerce.inventory.Inventory;
import com.mikey.ecommerce.inventory.InventoryRepository;
import com.mikey.ecommerce.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
@Tag(
        name = "Products",
        description = "Product catalog management endpoints for searching, viewing, creating, updating, and obsoleting products."
)
public class ProductController {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("name", "price", "createdAt");

    private static final Set<String> ALLOWED_SORT_DIRECTIONS =
            Set.of("asc", "desc");

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductController(
            ProductRepository productRepository,
            InventoryRepository inventoryRepository
    ) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping
    public PageResponse<ProductSummaryResponse> findAll(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "minPrice", defaultValue = "0") BigDecimal minPrice,
            @RequestParam(name = "maxPrice", defaultValue = "999999999") BigDecimal maxPrice,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir
    ) {
        validatePaginationAndSorting(page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products =
                productRepository.findByNameContainingIgnoreCaseAndPriceBetween(
                        search,
                        minPrice,
                        maxPrice,
                        pageable
                );

        List<ProductSummaryResponse> content =
                products.getContent()
                        .stream()
                        .map(ProductMapper::toSummary)
                        .toList();

        return new PageResponse<>(
                content,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isLast()
        );
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable("id") Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found"));

        int availableQuantity = inventoryRepository
                .findByProductId(product.getId())
                .map(Inventory::getQuantityAvailable)
                .orElse(0);

        return ProductMapper.toResponse(product, availableQuantity);
    }

    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        Product product = productRepository.save(
                new Product(
                        request.name(),
                        request.description(),
                        request.category(),
                        request.imageUrl(),
                        request.active() == null || request.active(),
                        request.price()
                )
        );

        int initialStock =
                request.initialStock() == null ? 0 : request.initialStock();

        inventoryRepository.save(new Inventory(product, initialStock));

        return ProductMapper.toResponse(product, initialStock);
    }

    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found"));

        product.update(
                request.name(),
                request.description(),
                request.category(),
                request.imageUrl(),
                request.active() == null || request.active(),
                request.price()
        );

        Product savedProduct = productRepository.save(product);

        int availableQuantity = inventoryRepository
                .findByProductId(savedProduct.getId())
                .map(Inventory::getQuantityAvailable)
                .orElse(0);

        return ProductMapper.toResponse(savedProduct, availableQuantity);
    }

    @DeleteMapping("/{id}")
    public ProductResponse obsolete(@PathVariable("id") Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found"));

        product.deactivate();

        Product savedProduct = productRepository.save(product);

        int availableQuantity = inventoryRepository
                .findByProductId(savedProduct.getId())
                .map(Inventory::getQuantityAvailable)
                .orElse(0);

        return ProductMapper.toResponse(savedProduct, availableQuantity);
    }

    private void validatePaginationAndSorting(
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        if (page < 0) {
            throw new ApiException("Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new ApiException("Page size must be between 1 and 100");
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new ApiException("Invalid sort field");
        }

        if (!ALLOWED_SORT_DIRECTIONS.contains(sortDir.toLowerCase())) {
            throw new ApiException("Invalid sort direction");
        }
    }
}