package com.ImageProcessing.Image_Processing_Service.controllers;

import com.ImageProcessing.Image_Processing_Service.security.jwt.JwtUtils;
import com.ImageProcessing.Image_Processing_Service.services.UserServiceImpl;
import com.ImageProcessing.Image_Processing_Service.model.User;
import com.ImageProcessing.Image_Processing_Service.payload.UserDTO;
import com.ImageProcessing.Image_Processing_Service.security.response.JwtResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Slf4j
public class AuthController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtService;


    @PostMapping("auth/register")
    public ResponseEntity<JwtResponse> register(@RequestBody UserDTO user)
    {
        User newUser = userService.signup(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);

        JwtResponse response = new JwtResponse();
        response.setUsername(user.getUsername());
        response.setJwtToken(token);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("auth/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user)
    {
        try{
            userService.authenticate(user);

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = jwtService.generateToken(userDetails);

            JwtResponse response = new JwtResponse();
            response.setUsername(user.getUsername());
            response.setJwtToken(token);

            return new ResponseEntity<>(response, HttpStatus.OK);

        }
        catch (BadCredentialsException e)
        {
            log.error("Bad credentials");
            return new ResponseEntity<>("Incorrect email or password",HttpStatus.NO_CONTENT);
        }

    }
}
