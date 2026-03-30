package me.jorlowski.pcms_demo.repository;

import me.jorlowski.pcms_demo.dto.producer.ProducerResponse;
import me.jorlowski.pcms_demo.model.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for {@link Producer} entities.
 * Includes specialized queries for aggregated data and existence validation.
 */
public interface ProducerRepository extends JpaRepository<Producer, Long> {

    /**
     * Retrieves all producers and calculates the total number of products for each.
     * Uses a JPQL constructor expression to directly project results into {@link ProducerResponse}
     * DTOs, avoiding the overhead of loading full entities.
     *
     * @return a list of {@link ProducerResponse} containing producer details and product counts
     */
    @Query("""
        SELECT new me.jorlowski.pcms_demo.dto.producer.ProducerResponse(
            p.id,
            p.name,
            CAST(COUNT(pr.id) AS int)
        )
        FROM Producer p
        LEFT JOIN Product pr ON pr.producer = p
        GROUP BY p.id, p.name
    """)
    List<ProducerResponse> findAllWithProductCount();

    /**
     * Checks if a producer with the given name already exists in the database,
     * performing a case-insensitive comparison.
     *
     * @param name the name to search for
     * @return true if a producer with the specified name exists (ignoring case), false otherwise
     */
    boolean existsByNameIgnoreCase(String name);
}