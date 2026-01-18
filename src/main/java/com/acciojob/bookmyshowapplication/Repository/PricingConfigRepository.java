package com.acciojob.bookmyshowapplication.Repository;

import com.acciojob.bookmyshowapplication.Enums.PricingFactorType;
import com.acciojob.bookmyshowapplication.Models.PricingConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PricingConfigRepository extends JpaRepository<PricingConfig, Integer> {

    List<PricingConfig> findByIsActiveTrue();

    List<PricingConfig> findByFactorTypeAndIsActiveTrue(PricingFactorType factorType);

    Optional<PricingConfig> findByConfigKeyAndIsActiveTrue(String configKey);

    @Query("SELECT pc FROM PricingConfig pc WHERE pc.factorType = :factorType " +
           "AND pc.isActive = true AND pc.minOccupancyPercent <= :occupancy " +
           "AND pc.maxOccupancyPercent > :occupancy")
    Optional<PricingConfig> findDemandBasedPricing(@Param("factorType") PricingFactorType factorType,
                                                     @Param("occupancy") Integer occupancy);

    @Query("SELECT pc FROM PricingConfig pc WHERE pc.factorType = :factorType " +
           "AND pc.isActive = true AND pc.startHour <= :hour AND pc.endHour > :hour")
    Optional<PricingConfig> findTimeBasedPricing(@Param("factorType") PricingFactorType factorType,
                                                  @Param("hour") Integer hour);
}
