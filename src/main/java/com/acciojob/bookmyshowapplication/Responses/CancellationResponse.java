package com.acciojob.bookmyshowapplication.Responses;

import com.acciojob.bookmyshowapplication.Enums.RefundStatus;
import com.acciojob.bookmyshowapplication.Enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancellationResponse {
    private String ticketId;
    private TicketStatus ticketStatus;
    private RefundStatus refundStatus;
    private Integer originalAmount;
    private Integer refundAmount;
    private Double refundPercentage;
    private Integer cancellationCharges;
    private String message;
    private LocalDateTime cancelledAt;
    private String estimatedRefundTime;
}
