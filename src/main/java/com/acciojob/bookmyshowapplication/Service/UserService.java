package com.acciojob.bookmyshowapplication.Service;

import com.acciojob.bookmyshowapplication.Models.User;
import com.acciojob.bookmyshowapplication.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public String addUser(User user){
        user = userRepository.save(user);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Welcome to Book Your Show Application");
        message.setFrom("springacciojob@gmail.com");
        message.setTo(user.getEmailId());

        String body = "Hi "+user.getName()+"! "+"\n"+
                "Welcome to Book your show Application !! , Feel free " +
                "to browse the movies and use Coupon START100 for an instant discount";

        message.setText(body);

        return "The user has been saved to the DB with userId"+user.getUserId();
    }
}