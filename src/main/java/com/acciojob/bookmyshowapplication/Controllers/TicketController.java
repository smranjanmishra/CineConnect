package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Models.Ticket;
import com.acciojob.bookmyshowapplication.Requests.BookTicketRequest;
import com.acciojob.bookmyshowapplication.Service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("bookTicket")
    public ResponseEntity bookTicket(@RequestBody BookTicketRequest bookTicketRequest){

        try {
            Ticket ticket = ticketService.bookTicket(bookTicketRequest);
            return new ResponseEntity(ticket, HttpStatus.OK);
        }
        catch (Exception e) {
            String errMsg = "Error while booking you tickets : "+e.getMessage();
            return new ResponseEntity(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}