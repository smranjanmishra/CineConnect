package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Models.PricingConfig;
import com.acciojob.bookmyshowapplication.Models.Show;
import com.acciojob.bookmyshowapplication.Repository.ShowRepository;
import com.acciojob.bookmyshowapplication.Responses.ApiResponse;
import com.acciojob.bookmyshowapplication.Responses.PricingResponse;
import com.acciojob.bookmyshowapplication.Service.DynamicPricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for dynamic pricing management
 */
@RestController
@RequestMapping("/api/v1/pricing")
@Tag(name = "Dynamic Pricing", description = "APIs for dynamic pricing management")
public class DynamicPricingController {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicPricingController.class);

    @Autowired
    private DynamicPricingService dynamicPricingService;

    @Autowired
    private ShowRepository showRepository;

    @GetMapping("/shows/{showId}")
    @Operation(summary = "Get show pricing", description = "Get the current dynamic pricing for a specific show")
    public ResponseEntity<ApiResponse<PricingResponse>> getShowPricing(@PathVariable Integer showId) {
        logger.info("Fetching pricing for show: {}", showId);
        
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new com.acciojob.bookmyshowapplication.Exceptions.ResourceNotFoundException("Show", "showId", showId));
        
        PricingResponse response = dynamicPricingService.calculateDynamicPricing(show);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/shows/{showId}/apply")
    @Operation(summary = "Apply dynamic pricing", description = "Apply dynamic pricing to a show's seats")
    public ResponseEntity<ApiResponse<String>> applyDynamicPricing(@PathVariable Integer showId) {
        logger.info("Applying dynamic pricing to show: {}", showId);
        
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new com.acciojob.bookmyshowapplication.Exceptions.ResourceNotFoundException("Show", "showId", showId));
        
        dynamicPricingService.applyDynamicPricingToShow(show);
        return ResponseEntity.ok(ApiResponse.success("Dynamic pricing applied successfully", "Pricing applied"));
    }

    @GetMapping("/configs")
    @Operation(summary = "Get pricing configs", description = "Get all pricing configuration rules")
    public ResponseEntity<ApiResponse<List<PricingConfig>>> getAllPricingConfigs() {
        logger.info("Fetching all pricing configs");
        
        List<PricingConfig> configs = dynamicPricingService.getAllPricingConfigs();
        return ResponseEntity.ok(ApiResponse.success(configs));
    }

    @GetMapping("/configs/active")
    @Operation(summary = "Get active configs", description = "Get all active pricing configuration rules")
    public ResponseEntity<ApiResponse<List<PricingConfig>>> getActivePricingConfigs() {
        logger.info("Fetching active pricing configs");
        
        List<PricingConfig> configs = dynamicPricingService.getActivePricingConfigs();
        return ResponseEntity.ok(ApiResponse.success(configs));
    }

    @PutMapping("/configs/{configId}")
    @Operation(summary = "Update pricing config", description = "Update a pricing configuration rule")
    public ResponseEntity<ApiResponse<PricingConfig>> updatePricingConfig(
            @PathVariable Integer configId,
            @Valid @RequestBody PricingConfig updatedConfig) {
        
        logger.info("Updating pricing config: {}", configId);
        
        PricingConfig config = dynamicPricingService.updatePricingConfig(configId, updatedConfig);
        return ResponseEntity.ok(ApiResponse.success("Pricing config updated successfully", config));
    }
}
