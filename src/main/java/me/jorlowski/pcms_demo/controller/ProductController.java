package me.jorlowski.pcms_demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.jorlowski.pcms_demo.dto.product.CreateProductRequest;
import me.jorlowski.pcms_demo.dto.product.ProductResponse;
import me.jorlowski.pcms_demo.dto.product.UpdateAttributesRequest;
import me.jorlowski.pcms_demo.dto.product.UpdateProductRequest;
import me.jorlowski.pcms_demo.service.ProductService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for managing products and their dynamic attributes.
 * Supports standard CRUD operations and advanced searching via PostgreSQL JSONB.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller", description = "Endpoints for managing products and dynamic specifications")
public class ProductController {

    private final ProductService productService;

    /**
     * Creates a new product associated with a specific producer.
     *
     * @param request the product data including name, producerId, and initial attributes map
     * @return the newly created product mapped to a response DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product")
    public ProductResponse createProduct(@RequestBody @Valid CreateProductRequest request) {
        return productService.createProduct(request);
    }

    /**
     * Searches for products using dynamic filters provided in query parameters.
     * Supports complex filtering like range queries (_min, _max), multi-value filters (IN),
     * and checking for attribute existence (has_attr).
     *
     * @param name             optional filter for product name (case-insensitive partial match)
     * @param allRequestParams a map containing all query parameters used for dynamic JSONB filtering
     * @param pageable         pagination and sorting information (default: 25 items per page, sorted by ID DESC)
     * @return a paginated result set of products matching the specified criteria
     */
    @GetMapping
    @Operation(summary = "Search products with dynamic filters")
    @Parameters({
            @Parameter(name = "name", description = "Filter by product name", example = "Smartphone"),
            @Parameter(name = "color", description = "Exact match for a specific JSONB attribute", example = "red"),
            @Parameter(name = "made_in", description = "Checks if attribute matches any value in a comma-separated list", example = "Poland,Germany"),
            @Parameter(name = "weight_min", description = "Numeric range filter: minimum value", example = "10.5"),
            @Parameter(name = "weight_max", description = "Numeric range filter: maximum value", example = "50.0"),
            @Parameter(name = "has_attr", description = "Comma-separated list of attribute keys that the product must possess", example = "weight,battery_mah")
    })
    public Page<ProductResponse> listProducts(
            @RequestParam(required = false) String name,
            @Parameter(hidden = true) @RequestParam Map<String, String> allRequestParams,
            @ParameterObject @PageableDefault(size = 25, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.search(name, allRequestParams, pageable);
    }

    /**
     * Updates the primary properties of an existing product (name and producer association).
     *
     * @param id      the unique identifier of the product to update
     * @param request the new values for name and producerId
     * @return the updated product data
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update product basic info", description = "Updates static fields like name and producerId")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProductRequest request
    ) {
        return productService.updateProduct(id, request);
    }

    /**
     * Performs a partial update on the product's dynamic attributes.
     * New keys are added, existing keys are updated. If a key's value is null, the attribute is removed.
     *
     * @param id         the unique identifier of the product
     * @param request    contains map of attribute keys and values to be merged or removed
     * @return the product data after attribute modification
     */
    @PatchMapping("{id}/attributes")
    @Operation(summary = "Patch product attributes", description = "Merges new attributes or removes them if value is null")
    public ProductResponse updateProductAttributes(
            @PathVariable Long id,
            @RequestBody @NonNull UpdateAttributesRequest request) {
        return productService.updateAttributes(id, request.attributes());
    }

    /**
     * Permanently deletes a product with the given ID from the system.
     *
     * @param id the unique identifier of the product to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a product")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}