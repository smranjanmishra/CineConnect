package com.acciojob.bookmyshowapplication.Models;

import com.acciojob.bookmyshowapplication.Enums.Genre;
import com.acciojob.bookmyshowapplication.Enums.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Entity representing a movie in the catalog
 */
@Entity
@Table(name = "movies")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Movie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;

    @NotBlank(message = "Movie name is required")
    @Size(min = 1, max = 200, message = "Movie name must be between 1 and 200 characters")
    @Column(unique = true)
    private String movieName;

    @Positive(message = "Duration must be positive")
    private double duration;

    @NotNull(message = "Genre is required")
    @Enumerated(value = EnumType.STRING)
    private Genre genre;

    @NotNull(message = "Release date is required")
    @PastOrPresent(message = "Release date must be in the past or present")
    private LocalDate releaseDate;
    
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 10, message = "Rating must be at most 10")
    private double rating;

    @NotNull(message = "Language is required")
    @Enumerated(value = EnumType.STRING)
    private Language language;
}