package com.acciojob.bookmyshowapplication.Models;

import com.acciojob.bookmyshowapplication.Enums.SeatType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "show_seats")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ShowSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer showSeatId;

    private String seatNo;

    @Enumerated(value = EnumType.STRING)
    private SeatType seatType;

    private Integer price;
    private Boolean isAvailable;

    @ManyToOne
    @JoinColumn
    private Show show;
}
