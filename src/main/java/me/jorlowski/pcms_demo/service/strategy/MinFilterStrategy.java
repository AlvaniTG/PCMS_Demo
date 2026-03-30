package me.jorlowski.pcms_demo.service.strategy;

import me.jorlowski.pcms_demo.model.Product;
import me.jorlowski.pcms_demo.repository.specification.ProductSpecs;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Strategy for filtering products with a numeric lower-bound constraint.
 * This strategy is triggered by query parameters ending with the "_min" suffix.
 * It ensures that the specified JSONB attribute's value is greater than or equal to the provided number.
 */
@Component
public class MinFilterStrategy implements ProductFilterStrategy {

    /**
     * Determines if the parameter key represents a minimum range filter.
     *
     * @param key   the query parameter key from the URL (e.g., "price_min")
     * @param value the query parameter value
     * @return true if the key ends with the reserved "_min" suffix
     */
    @Override
    public boolean isApplicable(String key, String value) {
        return key.endsWith("_min");
    }

    /**
     * Creates a numeric "greater than or equal" specification for a JSONB attribute.
     * Strips the "_min" suffix to identify the actual attribute name and validates
     * that the input value is a valid number.
     *
     * @param key   the query parameter key containing the suffix (e.g., "weight_min")
     * @param value the numeric threshold as a string
     * @return a specification that casts the JSONB value to a double and performs the comparison
     * @throws IllegalArgumentException if the provided value cannot be parsed as a Double
     */
    @Override
    public Specification<Product> createSpecification(String key, String value) {
        String attrName = key.replace("_min", "");
        try {
            Double numericValue = Double.parseDouble(value);
            return ProductSpecs.attributeGreaterOrEqual(attrName, numericValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Attribute '%s' must be numeric, but got: '%s'", attrName,  value)
            );
        }
    }

    /**
     * Checks if the given key is reserved for minimum range filtering.
     * Prevents users from creating static attributes ending with the "_min" suffix
     * to avoid conflicts with the range filtering engine.
     *
     * @param key the attribute key to check
     * @return true if the key ends with the reserved "_min" suffix (case-insensitive)
     */
    @Override
    public boolean isReserved(String key) {
        return key.toLowerCase().endsWith("_min");
    }
}