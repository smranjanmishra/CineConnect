package com.acciojob.bookmyshowapplication.Service;

import com.acciojob.bookmyshowapplication.Exceptions.ResourceNotFoundException;
import com.acciojob.bookmyshowapplication.Models.User;
import com.acciojob.bookmyshowapplication.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer for user management
 */
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Add a new user to the system
     */
    public String addUser(User user) {
        logger.info("Adding new user: {}", user.getMobNo());
        
        user = userRepository.save(user);
        logger.info("User created successfully with ID: {}", user.getUserId());
        
        return "User has been saved to the database with userId: " + user.getUserId();
    }
    
    /**
     * Get user by ID
     */
    public User getUserById(Integer userId) {
        logger.info("Fetching user with ID: {}", userId);
        
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
    }
    
    /**
     * Get user by mobile number
     */
    public User getUserByMobile(String mobNo) {
        logger.info("Fetching user with mobile: {}", mobNo);
        
        User user = userRepository.findUserByMobNo(mobNo);
        if (user == null) {
            throw new ResourceNotFoundException("User", "mobile number", mobNo);
        }
        return user;
    }
}