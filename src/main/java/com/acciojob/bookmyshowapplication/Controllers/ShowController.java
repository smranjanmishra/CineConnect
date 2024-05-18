package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Requests.AddShowRequest;
import com.acciojob.bookmyshowapplication.Requests.AddShowSeatsRequest;
import com.acciojob.bookmyshowapplication.Service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    @PostMapping("addShow")
    public String addShow(@RequestBody AddShowRequest showRequest) {

        String result = showService.addShows(showRequest);
        return result;
    }

    @PostMapping("addShowSeats")
    public String addShowSeats(@RequestBody AddShowSeatsRequest showSeatsRequest){

        String response = showService.addShowSeats(showSeatsRequest);
        return response;
    }
}