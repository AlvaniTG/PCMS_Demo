package me.jorlowski.pcms_demo.service;

import lombok.RequiredArgsConstructor;
import me.jorlowski.pcms_demo.dto.product.CreateProductRequest;
import me.jorlowski.pcms_demo.dto.product.ProductResponse;
import me.jorlowski.pcms_demo.dto.product.UpdateProductRequest;
import me.jorlowski.pcms_demo.mapper.ProductMapper;
import me.jorlowski.pcms_demo.model.Producer;
import me.jorlowski.pcms_demo.model.Product;
import me.jorlowski.pcms_demo.repository.ProducerRepository;
import me.jorlowski.pcms_demo.repository.ProductRepository;
import me.jorlowski.pcms_demo.repository.specification.ProductSpecs;
import me.jorlowski.pcms_demo.service.strategy.ProductFilterStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service orchestrating the business logic for {@link Product} entities.
 * Features a dynamic filtering engine powered by the Strategy pattern and PostgreSQL JSONB.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    /**
     * Parameters that should be ignored by the dynamic attribute filtering engine
     * as they serve technical purposes (pagination, sorting, or static fields).
     */
    private static final List<String> TECHNICAL_PARAMS = List.of("page", "size", "sort", "name");

    private final ProductRepository productRepository;
    private final ProducerRepository producerRepository;
    private final List<ProductFilterStrategy> filterStrategies;
    private final ProductMapper productMapper;

    /**
     * Creates a new product and associates it with a validated {@link Producer}.
     *
     * @param request the product details including name, producer ID, and initial attributes
     * @return a {@link ProductResponse} containing the persisted product data
     * @throws RuntimeException if the specified producer does not exist
     */
    public ProductResponse createProduct(CreateProductRequest request) {
        validateAttributeKeys(request.attributes());

        Producer producer = producerRepository.findById(request.producerId())
                .orElseThrow(() -> new RuntimeException("Producer not found"));

        Product product = new Product();
        product.setName(request.name());
        product.setProducer(producer);
        product.setAttributes(request.attributes() != null ? request.attributes() : new HashMap<>());

        return mapToResponse(productRepository.save(product));
    }

    /**
     * Updates product attributes using a merge-or-remove strategy.
     * Values provided as {@code null} in the map will cause the corresponding key
     * to be removed from the JSONB storage.
     *
     * @param id            the unique identifier of the product to update
     * @param newAttributes a map containing attribute updates or removals
     * @return the updated {@link ProductResponse}
     * @throws RuntimeException if the product is not found
     */
    public ProductResponse updateAttributes(Long id, Map<String, Object> newAttributes) {
        validateAttributeKeys(newAttributes);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Map<String, Object> currentAttributes = product.getAttributes();
        newAttributes.forEach((key, value) -> {
            if (value == null) {
                currentAttributes.remove(key);
            } else {
                currentAttributes.put(key, value);
            }
        });
        product.setAttributes(currentAttributes);
        return mapToResponse(productRepository.save(product));
    }

    /**
     * Performs a multi-criteria search using dynamic filters and pagination.
     * Iterates through registered {@link ProductFilterStrategy} implementations to
     * build a combined JPA Specification. Filters are applied using logical AND.
     *
     * @param name      optional partial name for case-insensitive matching
     * @param allParams raw query parameters from the request to be resolved into specifications
     * @param pageable  pagination and sorting details
     * @return a paginated result set of {@link ProductResponse} objects
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String name, Map<String, String> allParams, Pageable pageable) {
        List<Specification<Product>> specifications = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            specifications.add(ProductSpecs.hasNameLike(name));
        }

        var sortedStrategies = filterStrategies.stream()
                .sorted(Comparator.comparingInt(ProductFilterStrategy::getOrder))
                .toList();

        allParams.forEach((key, value) -> {
            if (TECHNICAL_PARAMS.contains(key)) return;

            sortedStrategies.stream()
                    .filter(s -> s.isApplicable(key, value))
                    .findFirst()
                    .ifPresent(s -> specifications.add(s.createSpecification(key, value)));
        });

        return productRepository.findAll(Specification.allOf(specifications), pageable)
                .map(productMapper::toResponse);
    }

    /**
     * Updates core product fields (name and producer link).
     * Re-associates the product with a new producer if the ID has changed.
     *
     * @param id      the unique identifier of the product
     * @param request the new name and producer ID
     * @return the updated {@link ProductResponse}
     * @throws RuntimeException if the product or the new producer is not found
     */
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getProducer().getId().equals(request.producerId())) {
            Producer newProducer = producerRepository.findById(request.producerId())
                    .orElseThrow(() -> new RuntimeException("Producer not found"));
            product.setProducer(newProducer);
        }

        product.setName(request.name());

        return mapToResponse(productRepository.save(product));
    }

    /**
     * Permanently removes a product from the database.
     *
     * @param id the unique identifier of the product to delete
     * @throws RuntimeException if the product does not exist
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    /**
     * Helper method to convert an entity to a response DTO.
     *
     * @param product the source entity
     * @return the mapped DTO
     */
    private ProductResponse mapToResponse(Product product) {
        return productMapper.toResponse(product);
    }

    /**
     * Dynamically validates attribute keys against all registered filtering strategies.
     * This ensures that adding a new strategy (e.g., DateFilterStrategy)
     * automatically protects its reserved keywords without modifying this service.
     */
    private void validateAttributeKeys(Map<String, Object> attributes) {
        if (attributes == null) return;

        attributes.keySet().forEach(key -> {
            String lowerCaseKey = key.toLowerCase();
            if (TECHNICAL_PARAMS.contains(lowerCaseKey)) {
                throw new IllegalArgumentException("Key '" + key + "' is a reserved technical parameter.");
            }

            boolean isReserved = filterStrategies.stream()
                    .anyMatch(strategy -> strategy.isReserved(lowerCaseKey));

            if (isReserved) {
                throw new IllegalArgumentException(
                        "Key '" + key + "' is reserved for filtering logic and cannot be used as an attribute name."
                );
            }
        });
    }
}