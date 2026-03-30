package me.jorlowski.pcms_demo.repository;

import me.jorlowski.pcms_demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository interface for {@link Product} entities.
 * Extends {@link JpaSpecificationExecutor} to support dynamic, multi-criteria
 * filtering using JPA Specifications and PostgreSQL JSONB functions.
 */
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

}
