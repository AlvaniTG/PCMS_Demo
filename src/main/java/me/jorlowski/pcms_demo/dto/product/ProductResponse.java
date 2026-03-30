package me.jorlowski.pcms_demo.dto.product;

import java.util.Map;

/**
 * Data Transfer Object representing the full details of a product.
 * This response includes flattened producer information for easier consumption by API clients,
 * avoiding the need for additional lookups.
 *
 * @param id           The unique database identifier of the product.
 * @param name         The name of the product.
 * @param producerId   The unique identifier of the associated producer.
 * @param producerName The human-readable name of the producer, retrieved during the mapping process.
 * @param attributes   The dynamic map of product specifications retrieved from the JSONB storage.
 */
public record ProductResponse(
        Long id,
        String name,
        Long producerId,
        String producerName,
        Map<String, Object> attributes
) {
}