package com.acciojob.bookmyshowapplication.Controllers;

import com.acciojob.bookmyshowapplication.Models.User;
import com.acciojob.bookmyshowapplication.Responses.ApiResponse;
import com.acciojob.bookmyshowapplication.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user management operations
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Create user", description = "Register a new user in the system")
    public ResponseEntity<ApiResponse<String>> createUser(@Valid @RequestBody User user) {
        logger.info("Creating new user with mobile: {}", user.getMobNo());
        
        String response = userService.addUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by user ID")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Integer userId) {
        logger.info("Fetching user with ID: {}", userId);
        
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @GetMapping("/mobile/{mobNo}")
    @Operation(summary = "Get user by mobile", description = "Retrieve user details by mobile number")
    public ResponseEntity<ApiResponse<User>> getUserByMobile(@PathVariable String mobNo) {
        logger.info("Fetching user with mobile: {}", mobNo);
        
        User user = userService.getUserByMobile(mobNo);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
