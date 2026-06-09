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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * REST controller responsible for product catalog management.
 *
 * <p>Provides endpoints for:</p>
 * <ul>
 *     <li>Searching and filtering products</li>
 *     <li>Retrieving product details by ID</li>
 *     <li>Creating new products</li>
 *     <li>Updating existing products</li>
 *     <li>Deleting products</li>
 * </ul>
 *
 * <p>When a product is created, a corresponding inventory record is
 * automatically initialized using the supplied initial stock quantity.</p>
 */
@RestController
@RequestMapping("/api/products")
@Tag(
        name = "Products",
        description = "Product catalog management endpoints for searching, viewing, creating, updating, and deleting products."
)
public class ProductController {

    /**
     * Fields that clients are allowed to sort by.
     */
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("name", "price", "createdAt");

    /**
     * Supported sort directions.
     */
    private static final Set<String> ALLOWED_SORT_DIRECTIONS =
            Set.of("asc", "desc");

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductController(ProductRepository productRepository,
                             InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Retrieves a paginated list of products with optional search,
     * price filtering, and sorting.
     *
     * @param search   optional product name search term
     * @param minPrice minimum product price
     * @param maxPrice maximum product price
     * @param page     zero-based page number
     * @param size     number of records per page
     * @param sortBy   field to sort by
     * @param sortDir  sort direction (asc or desc)
     * @return paginated response containing product summaries
     */
    @Operation(
            summary = "Search and list products",
            description = "Returns a paginated list of products. Supports optional text search, price range filtering, pagination, and sorting by name, price, or createdAt."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            )
    })
    @GetMapping
    public PageResponse<ProductSummaryResponse> findAll(
            @Parameter(description = "Search term to filter products by name", example = "Laptop")
            @RequestParam(name = "search", defaultValue = "") String search,

            @Parameter(description = "Minimum product price", example = "0")
            @RequestParam(name = "minPrice", defaultValue = "0") BigDecimal minPrice,

            @Parameter(description = "Maximum product price", example = "999999999")
            @RequestParam(name = "maxPrice", defaultValue = "999999999") BigDecimal maxPrice,

            @Parameter(description = "Zero-based page number", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,

            @Parameter(description = "Number of records per page (1-100)", example = "10")
            @RequestParam(name = "size", defaultValue = "10") int size,

            @Parameter(description = "Sort field: name, price, or createdAt", example = "name")
            @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,

            @Parameter(description = "Sort direction: asc or desc", example = "asc")
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

    /**
     * Retrieves a single product by its unique identifier.
     *
     * @param id product ID
     * @return detailed product response
     */
    @Operation(
            summary = "Get product by ID",
            description = "Retrieves detailed information for a single product using its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @GetMapping("/{id}")
    public ProductResponse findById(
            @Parameter(description = "Unique product ID", example = "101")
            @PathVariable("id") Long id
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found"));

        return ProductMapper.toResponse(product);
    }

    /**
     * Creates a new product and initializes inventory.
     *
     * @param request product creation request
     * @return created product response
     */
    @Operation(
            summary = "Create product",
            description = "Creates a new product and automatically initializes its inventory record. If initialStock is omitted, the stock quantity defaults to 0."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            )
    })
    @PostMapping
    public ProductResponse create(
            @Valid
            @RequestBody ProductRequest request
    ) {
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

        return ProductMapper.toResponse(product);
    }

    /**
     * Updates an existing product.
     *
     * @param id      product ID
     * @param request updated product details
     * @return updated product response
     */
    @Operation(
            summary = "Update product",
            description = "Updates an existing product's name, description, and price."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @PutMapping("/{id}")
    public ProductResponse update(
            @Parameter(description = "Unique product ID", example = "101")
            @PathVariable("id") Long id,

            @Valid
            @RequestBody ProductRequest request
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

        return ProductMapper.toResponse(savedProduct);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id product ID
     */
    @Operation(
            summary = "Delete product",
            description = "Deletes a product by its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "Unique product ID", example = "101")
            @PathVariable("id") Long id
    ) {
        if (!productRepository.existsById(id)) {
            throw new ApiException("Product not found");
        }

        productRepository.deleteById(id);
    }

    /**
     * Validates pagination and sorting parameters.
     *
     * @param page    page number
     * @param size    page size
     * @param sortBy  sort field
     * @param sortDir sort direction
     */
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