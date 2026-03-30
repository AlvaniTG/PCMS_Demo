package me.jorlowski.pcms_demo.service;

import lombok.RequiredArgsConstructor;
import me.jorlowski.pcms_demo.dto.producer.ProducerResponse;
import me.jorlowski.pcms_demo.model.Producer;
import me.jorlowski.pcms_demo.repository.ProducerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsible for managing the lifecycle of {@link Producer} entities.
 * Handles business logic for manufacturer creation, retrieval, and removal.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProducerService {

    private final ProducerRepository producerRepository;

    /**
     * Retrieves all producers with their associated product counts.
     * This method is optimized with read-only transaction settings.
     *
     * @return a list of {@link ProducerResponse} containing manufacturer details and stats
     */
    @Transactional(readOnly = true)
    public List<ProducerResponse> getAllProducers() {
        return producerRepository.findAllWithProductCount();
    }

    /**
     * Creates and persists a new producer after performing name validation.
     * Checks for name presence and ensures uniqueness (case-insensitive).
     *
     * @param name the unique name of the producer to be created
     * @return a {@link ProducerResponse} representing the newly saved producer
     * @throws IllegalArgumentException if the provided name is null or blank
     * @throws RuntimeException if a producer with the same name already exists
     */
    public ProducerResponse createProducer(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        if (producerRepository.existsByNameIgnoreCase(name)) {
            throw new RuntimeException("Producer with name " + name + " already exists");
        }

        Producer producer = new Producer();
        producer.setName(name.trim());
        Producer saved =  producerRepository.save(producer);

        return new ProducerResponse(saved.getId(), saved.getName(), 0);
    }

    /**
     * Removes a producer from the system based on its unique identifier.
     * Note: Deletion triggers a cascade removal of all associated products.
     *
     * @param id the unique identifier of the producer to delete
     * @throws RuntimeException if no producer is found with the given ID
     */
    public void deleteProducer(Long id) {
        if (!producerRepository.existsById(id)) {
            throw new RuntimeException("Producer with id " + id + " does not exist");
        }
        producerRepository.deleteById(id);
    }
}