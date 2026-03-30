package me.jorlowski.pcms_demo.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Data Transfer Object for creating a new product.
 * Encapsulates the mandatory basic information and the initial set of dynamic attributes.
 *
 * @param name       The name of the product (must not be blank).
 * @param producerId The unique identifier of the manufacturer this product belongs to (must not be null).
 * @param attributes A flexible map of additional product specifications, stored as JSONB in the database.
 */
public record CreateProductRequest(
        @NotBlank String name,
        @NotNull Long producerId,
        Map<String, Object> attributes
) {
}