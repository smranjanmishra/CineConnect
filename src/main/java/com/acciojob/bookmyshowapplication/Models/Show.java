package com.acciojob.bookmyshowapplication.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "shows")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer showId;
    private LocalDate showDate; //"YYYY-MM-DD"
    private LocalTime showTime; //"HH:MM:SS"

    @ManyToOne
    @JoinColumn
    private Movie movie;

    @ManyToOne
    @JoinColumn
    private Theater theater;
}
