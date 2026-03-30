package me.jorlowski.pcms_demo.repository.specification;

import me.jorlowski.pcms_demo.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Utility class for building JPA Specifications targeting PostgreSQL JSONB columns.
 * Provides dynamic filtering capabilities for products with complex attribute sets.
 */
public class ProductSpecs {

    /**
     * Builds a specification to check if a specific key has an assigned exact value.
     * @param key The JSONB attribute key to search for.
     * @param value The exact string value to match.
     * @return A specification that extracts the attribute as text and compares it to the value.
     */
    public static Specification<Product> hasAttribute(String key, String value) {
        return (root, query, cb) -> cb.equal(
                cb.function("jsonb_extract_path_text", String.class,
                        root.get("attributes"), cb.literal(key)),
                value
        );
    }

    /**
     * Builds a specification to verify the existence of a key within the attribute map.
     * @param key The attribute key that must exist in the product.
     * @return A specification using the PostgreSQL '??' (jsonb_exists) operator.
     */
    public static Specification<Product> hasAttributeKey(String key) {
        return (root, query, cb) -> cb.isTrue(
                cb.function("jsonb_exists", Boolean.class, root.get("attributes"), cb.literal(key))
        );
    }

    /**
     * Builds a specification to check if an attribute's value matches any in the provided list.
     * @param key The JSONB attribute key.
     * @param values A list of strings representing potential matching values (OR logic).
     * @return A specification using the SQL 'IN' operator on the extracted JSONB text.
     */
    public static Specification<Product> hasAttributeIn(String key, List<String> values) {
        return (root, query, cb) -> {
            var jsonValue = cb.function("jsonb_extract_path_text", String.class,
                    root.get("attributes"), cb.literal(key));
            return jsonValue.in(values);
        };
    }

    /**
     * Performs a case-insensitive search on the product's name.
     * @param name The partial name or keyword to search for.
     * @return A specification that wraps the name in '%' wildcards for a 'LIKE' comparison.
     */
    public static Specification<Product> hasNameLike(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    /**
     * Builds a specification for a 'greater than or equal' comparison on a numeric attribute.
     * @param key The JSONB attribute key (must contain numeric data).
     * @param value The minimum threshold value.
     * @return A specification that casts the JSONB text value to 'float8' for numeric comparison.
     */
    public static Specification<Product> attributeGreaterOrEqual(String key, Double value) {
        return (root, query, cb) -> {
            var textValue = cb.function("jsonb_extract_path_text", String.class,
                    root.get("attributes"), cb.literal(key));

            var numericValue = cb.function("float8", Double.class, textValue);

            return cb.greaterThanOrEqualTo(numericValue, value);
        };
    }

    /**
     * Builds a specification for a 'less than or equal' comparison on a numeric attribute.
     * @param key The JSONB attribute key (must contain numeric data).
     * @param value The maximum threshold value.
     * @return A specification that casts the JSONB text value to 'float8' for numeric comparison.
     */
    public static Specification<Product> attributeLessOrEqual(String key, Double value) {
        return (root, query, cb) -> {
            var textValue = cb.function("jsonb_extract_path_text", String.class,
                    root.get("attributes"), cb.literal(key));

            var numericValue = cb.function("float8", Double.class, textValue);

            return cb.lessThanOrEqualTo(numericValue, value);
        };
    }
}
