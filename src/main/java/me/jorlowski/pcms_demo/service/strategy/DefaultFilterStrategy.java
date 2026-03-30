package me.jorlowski.pcms_demo.service.strategy;

import me.jorlowski.pcms_demo.model.Product;
import me.jorlowski.pcms_demo.repository.specification.ProductSpecs;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Fallback filtering strategy for exact attribute matching.
 * This strategy is designed to handle all parameters not captured by
 * more specific strategies (like range or multi-value filters).
 */
@Component
public class DefaultFilterStrategy implements ProductFilterStrategy {

    /**
     * Always returns true as this is the default catch-all strategy.
     * @param key   the attribute key from the query parameters
     * @param value the value to filter by
     * @return true regardless of the input
     */
    @Override
    public boolean isApplicable(String key, String value) {
        return true;
    }

    /**
     * Creates a specification for an exact match of a JSONB attribute value.
     * @param key the JSONB key to search for
     * @param value the exact string value to match
     * @return a specification that performs an equality check on the JSONB text field
     */
    @Override
    public Specification<Product> createSpecification(String key, String value) {
        return ProductSpecs.hasAttribute(key, value);
    }

    /**
     * Returns the lowest possible priority to ensure this strategy is
     * evaluated only after all other specific strategies have been checked.
     * @return {@link Integer#MAX_VALUE} representing the lowest priority
     */
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}