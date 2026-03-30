package me.jorlowski.pcms_demo.service.strategy;

import me.jorlowski.pcms_demo.model.Product;
import me.jorlowski.pcms_demo.repository.specification.ProductSpecs;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Strategy for multi-value attribute filtering.
 * This strategy is triggered when a query parameter value contains a comma,
 * representing a list of potential matches for a single JSONB attribute.
 */
@Component
public class InFilterStrategy implements ProductFilterStrategy {

    /**
     * Checks if the value contains a comma and the key is not the reserved "has_attr".
     *
     * @param key   the query parameter key from the URL
     * @param value the query parameter value string
     * @return true if the value represents a list and the key is a standard attribute
     */
    @Override
    public boolean isApplicable(String key, String value) {
        return value.contains(",") && !"has_attr".equals(key);
    }

    /**
     * Creates a specification that matches a JSONB attribute against a list of values.
     * The input value is split by commas to create an 'IN' clause in the generated SQL.
     *
     * @param key   the JSONB attribute key to filter by
     * @param value a comma-separated string of possible values (e.g., "Poland,Germany")
     * @return a specification that evaluates to true if the attribute matches any value in the list
     */
    @Override
    public Specification<Product> createSpecification(String key, String value) {
        return ProductSpecs.hasAttributeIn(key, Arrays.asList(value.split(",")));
    }
}