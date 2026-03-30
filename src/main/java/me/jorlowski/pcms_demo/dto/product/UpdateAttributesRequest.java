package me.jorlowski.pcms_demo.dto.product;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Data Transfer Object used for patching a product's dynamic attributes.
 * This request encapsulates the changes to be merged into the existing JSONB attribute map.
 *
 * @param attributes A map of attribute keys and their new values.
 * Important: Providing a null value for a key will trigger its
 * removal from the product's specification storage.
 */
public record UpdateAttributesRequest(
        @NotNull Map<String, Object> attributes
) {
}