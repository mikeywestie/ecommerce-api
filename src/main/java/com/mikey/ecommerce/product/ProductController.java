package com.mikey.ecommerce.product;

import com.mikey.ecommerce.common.ApiException;
import com.mikey.ecommerce.dto.common.PageResponse;
import com.mikey.ecommerce.dto.product.ProductResponse;
import com.mikey.ecommerce.dto.product.ProductSummaryResponse;
import com.mikey.ecommerce.inventory.Inventory;
import com.mikey.ecommerce.inventory.InventoryRepository;
import com.mikey.ecommerce.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Products")
public class ProductController {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("name", "price", "createdAt");

    private static final Set<String> ALLOWED_SORT_DIRECTIONS =
            Set.of("asc", "desc");

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductController(ProductRepository productRepository,
                             InventoryRepository inventoryRepository) {
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

    @Operation(
            summary = "Get product by ID",
            description = "Fetches a single product's details using its unique numeric ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product found successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @GetMapping("/{id}")
    public ProductResponse findById(
            @Parameter(description = "The ID of the product to retrieve", example = "101")
            @PathVariable("id") Long id
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found"));

        return ProductMapper.toResponse(product);
    }

    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        Product product = productRepository.save(
                new Product(request.name(), request.description(), request.price())
        );

        int initialStock = request.initialStock() == null ? 0 : request.initialStock();

        inventoryRepository.save(new Inventory(product, initialStock));

        return ProductMapper.toResponse(product);
    }

    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found"));

        product.update(request.name(), request.description(), request.price());

        Product savedProduct = productRepository.save(product);

        return ProductMapper.toResponse(savedProduct);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        if (!productRepository.existsById(id)) {
            throw new ApiException("Product not found");
        }

        productRepository.deleteById(id);
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