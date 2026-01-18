package com.acciojob.bookmyshowapplication.Requests;

import lombok.Data;

@Data
public class CancelTicketRequest {
    private String ticketId;
    private String cancellationReason;
}
