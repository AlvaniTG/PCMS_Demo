package me.jorlowski.pcms_demo.dto.producer;

/**
 * Data Transfer Object representing a producer's summary information.
 * This record is used to transport producer data along with aggregated statistics.
 *
 * @param id           The unique identifier of the producer.
 * @param name         The official name of the manufacturer.
 * @param productCount The total number of products currently associated with this producer.
 */
public record ProducerResponse(
        Long id,
        String name,
        int productCount
) {
}
