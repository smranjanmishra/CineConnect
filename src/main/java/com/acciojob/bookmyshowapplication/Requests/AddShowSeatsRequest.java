package com.acciojob.bookmyshowapplication.Requests;

import jakarta.persistence.Id;
import lombok.Data;

@Data
public class AddShowSeatsRequest {

    private Integer showId;
    private Integer priceOfClassicSeats;
    private Integer priceOfPremiumSeats;
}
