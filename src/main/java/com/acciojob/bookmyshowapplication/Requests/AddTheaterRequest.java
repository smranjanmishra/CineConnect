package com.acciojob.bookmyshowapplication.Requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddTheaterRequest {

    private String name;
    private String address;
    private Integer noOfScreens;
}
