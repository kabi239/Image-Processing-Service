package com.ImageProcessing.Image_Processing_Service.services;

import com.ImageProcessing.Image_Processing_Service.model.User;
import com.ImageProcessing.Image_Processing_Service.payload.UserDTO;
import com.ImageProcessing.Image_Processing_Service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;


    public User signup(UserDTO user)
    {
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepo.save(newUser);

        return newUser;
    }

    public void authenticate(UserDTO user) {

        try{
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());

            authenticationManager.authenticate(authenticationToken);
        }
        catch (BadCredentialsException exception)
        {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }
    }
}
