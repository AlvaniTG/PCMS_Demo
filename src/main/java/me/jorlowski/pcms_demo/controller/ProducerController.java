package me.jorlowski.pcms_demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import me.jorlowski.pcms_demo.dto.producer.ProducerResponse;
import me.jorlowski.pcms_demo.service.ProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing product producers/manufacturers.
 * Provides endpoints to list, create, and remove producers from the system.
 */
@RestController
@RequestMapping("/api/v1/producers")
@RequiredArgsConstructor
@Tag(name = "Producer Controller", description = "Endpoints for managing product manufacturers")
public class ProducerController {

    private final ProducerService producerService;

    /**
     * Retrieves all producers along with the count of products associated with each.
     * @return a list of all producers and their aggregated product statistics
     */
    @GetMapping
    @Operation(summary = "Get all producers", description = "Returns a list of all producers with their respective product counts")
    public List<ProducerResponse> getAllProducers() {
        return producerService.getAllProducers();
    }

    /**
     * Creates a new producer in the system.
     * @param name the unique name of the manufacturer to be created
     * @return the newly created producer data including its generated ID
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new producer")
    public ProducerResponse createProducer(
            @Parameter(description = "Unique name of the producer", example = "Samsung")
            @RequestParam @NotBlank String name) {
        return producerService.createProducer(name);
    }

    /**
     * Removes a producer from the system and all its associated products (cascading).
     * @param id the unique identifier of the producer to be removed
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a producer", description = "Deletes a producer and triggers a cascade delete for all its products")
    public void deleteProducer(@PathVariable Long id) {
        producerService.deleteProducer(id);
    }
}