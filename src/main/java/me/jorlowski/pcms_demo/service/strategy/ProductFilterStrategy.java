package me.jorlowski.pcms_demo.service.strategy;

import me.jorlowski.pcms_demo.model.Product;
import org.springframework.data.jpa.domain.Specification;

/**
 * Strategy interface for resolving dynamic product filters from URL query parameters.
 * This interface follows the Strategy Pattern to decouple the filtering logic
 * from the main service, allowing for easy extension of filtering capabilities.
 */
public interface ProductFilterStrategy {

    /**
     * Determines whether this strategy is responsible for handling the given parameter.
     *
     * @param key   the query parameter key (e.g., "color", "weight_min", "has_attr")
     * @param value the query parameter value from the URL
     * @return true if this strategy can process the provided key-value pair, false otherwise
     */
    boolean isApplicable(String key, String value);

    /**
     * Transforms the URL parameter into a JPA Specification for the {@link Product} entity.
     * This method is only called if {@link #isApplicable(String, String)} returns true.
     *
     * @param key   the attribute key to be filtered
     * @param value the filter criteria or value(s)
     * @return a {@link Specification} representing the database filter, or null if no filter is created
     */
    Specification<Product> createSpecification(String key, String value);

    /**
     * Checks if a given key is reserved by this strategy and cannot be used
     * as a standard attribute name in the database.
     *
     * @param key the attribute key to check
     * @return true if the key is reserved for filtering logic
     */
    default boolean isReserved(String key) {
        return false;
    }

    /**
     * Defines the execution order of the strategy.
     * Lower values have higher priority. This is crucial for ensuring that specific
     * strategies (like range filters) are evaluated before catch-all strategies.
     *
     * @return the priority order of this strategy (default is 0)
     */
    default int getOrder() {
        return 0;
    }
}