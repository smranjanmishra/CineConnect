package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Models.User;
import com.acciojob.bookmyshowapplication.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")

public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("addUser")
    public String addUser(@RequestBody User user){
        String response = userService.addUser(user);
        return response;
    }
}