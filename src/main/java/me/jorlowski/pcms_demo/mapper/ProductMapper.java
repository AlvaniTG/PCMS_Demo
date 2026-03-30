package me.jorlowski.pcms_demo.mapper;

import me.jorlowski.pcms_demo.dto.product.ProductResponse;
import me.jorlowski.pcms_demo.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Component responsible for mapping Product entities to their respective DTOs.
 * Utilizes MapStruct for high-performance, compile-time mapping.
 * This mapper is integrated into the Spring Application Context.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Converts a Product entity into a ProductResponse DTO.
     * Flattens the nested Producer information (ID and Name) into the top-level DTO fields.
     *
     * @param product the source Product entity containing nested Producer and JSONB attributes
     * @return a mapped ProductResponse DTO ready for API delivery
     */
    @Mapping(source = "producer.id", target = "producerId")
    @Mapping(source = "producer.name", target = "producerName")
    ProductResponse toResponse(Product product);
}