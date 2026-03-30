package me.jorlowski.pcms_demo.service.strategy;

import me.jorlowski.pcms_demo.model.Product;
import me.jorlowski.pcms_demo.repository.specification.ProductSpecs;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Strategy for filtering products that must contain specific attribute keys.
 * This strategy handles the reserved "has_attr" query parameter.
 * It ensures that the product's JSONB attributes map includes all requested keys.
 */
@Component
public class HasAttributeKeyStrategy implements ProductFilterStrategy {

    /**
     * Determines if the current parameter is the reserved "has_attr" keyword.
     *
     * @param key   the query parameter key from the URL
     * @param value the query parameter value (ignored for applicability check)
     * @return true if the key matches "has_attr"
     */
    @Override
    public boolean isApplicable(String key, String value) {
        return "has_attr".equals(key);
    }

    /**
     * Creates a combined specification that checks for the existence of multiple JSONB keys.
     * The input string is split by commas, and each key is combined using a logical AND.
     *
     * @param key   the reserved keyword "has_attr"
     * @param value a comma-separated list of required attribute keys (e.g., "weight,color")
     * @return a specification that evaluates to true only if ALL specified keys exist
     */
    @Override
    public Specification<Product> createSpecification(String key, String value) {
        return Arrays.stream(value.split(","))
                .map(ProductSpecs::hasAttributeKey)
                .reduce(Specification::and)
                .orElse(null);
    }

    /**
     * Returns the priority of this strategy.
     * A value of 100 ensures it is evaluated before the {@link DefaultFilterStrategy}.
     *
     * @return the execution order priority
     */
    @Override
    public int getOrder() {
        return 100;
    }

    /**
     * Checks if the given key matches the reserved "has_attr" keyword.
     * Prevents users from creating a static attribute named exactly "has_attr",
     * as this keyword is used to verify the existence of other attributes.
     *
     * @param key the attribute key to check
     * @return true if the key is equal to "has_attr" (case-insensitive)
     */
    @Override
    public boolean isReserved(String key) {
        return "has_attr".equalsIgnoreCase(key);
    }
}