package me.jorlowski.pcms_demo.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for updating a product's primary information.
 * This request is typically used for 'PUT' operations to modify the
 * core fields that are common across all product types.
 *
 * @param name       The updated name of the product (must not be blank).
 * @param producerId The unique identifier of the manufacturer (must not be null).
 * Changing this will re-associate the product with a different producer.
 */
public record UpdateProductRequest(
        @NotBlank String name,
        @NotNull Long producerId
) {
}