package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Models.PricingConfig;
import com.acciojob.bookmyshowapplication.Models.Show;
import com.acciojob.bookmyshowapplication.Repository.ShowRepository;
import com.acciojob.bookmyshowapplication.Responses.PricingResponse;
import com.acciojob.bookmyshowapplication.Service.DynamicPricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/pricing")
@Tag(name = "Dynamic Pricing", description = "APIs for dynamic pricing management")
public class DynamicPricingController {

    @Autowired
    private DynamicPricingService dynamicPricingService;

    @Autowired
    private ShowRepository showRepository;

    @GetMapping("/show/{showId}")
    @Operation(summary = "Get dynamic pricing for show", description = "Get the current dynamic pricing for a specific show")
    public ResponseEntity<?> getShowPricing(@PathVariable Integer showId) {
        try {
            Show show = showRepository.findById(showId)
                    .orElseThrow(() -> new Exception("Show not found"));
            
            PricingResponse response = dynamicPricingService.calculateDynamicPricing(show);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error fetching pricing: " + e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @PostMapping("/apply/{showId}")
    @Operation(summary = "Apply dynamic pricing", description = "Apply dynamic pricing to a show's seats")
    public ResponseEntity<?> applyDynamicPricing(@PathVariable Integer showId) {
        try {
            Show show = showRepository.findById(showId)
                    .orElseThrow(() -> new Exception("Show not found"));
            
            dynamicPricingService.applyDynamicPricingToShow(show);
            return new ResponseEntity<>("Dynamic pricing applied successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error applying pricing: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/configs")
    @Operation(summary = "Get all pricing configs", description = "Get all pricing configuration rules")
    public ResponseEntity<?> getAllPricingConfigs() {
        try {
            List<PricingConfig> configs = dynamicPricingService.getAllPricingConfigs();
            return new ResponseEntity<>(configs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error fetching configs: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/configs/active")
    @Operation(summary = "Get active pricing configs", description = "Get all active pricing configuration rules")
    public ResponseEntity<?> getActivePricingConfigs() {
        try {
            List<PricingConfig> configs = dynamicPricingService.getActivePricingConfigs();
            return new ResponseEntity<>(configs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error fetching configs: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/configs/{configId}")
    @Operation(summary = "Update pricing config", description = "Update a pricing configuration rule")
    public ResponseEntity<?> updatePricingConfig(
            @PathVariable Integer configId,
            @RequestBody PricingConfig updatedConfig) {
        try {
            PricingConfig config = dynamicPricingService.updatePricingConfig(configId, updatedConfig);
            return new ResponseEntity<>(config, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error updating config: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
