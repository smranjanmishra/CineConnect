package com.acciojob.bookmyshowapplication.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.apache.catalina.User;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tickets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String ticketId;

    private String movieName;
    private LocalDate showDate;
    private LocalTime showTime;
    private String theaterNameAndAddress;
    private Integer totalAmtPaid;

    @ManyToOne
    @JoinColumn
    private User user;
}
