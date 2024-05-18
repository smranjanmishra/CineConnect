package com.acciojob.bookmyshowapplication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UpdateMovieRequest {
    private Integer movieId;
    private double rating;
    private double duration;
}
