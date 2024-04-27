package com.monks.electronic.store.controllers;

import com.monks.electronic.store.dtos.JwtRequest;
import com.monks.electronic.store.dtos.JwtResponse;
import com.monks.electronic.store.dtos.UserDTO;
import com.monks.electronic.store.exceptions.BadApiRequest;
import com.monks.electronic.store.security.JwtHelper;
import com.monks.electronic.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService; // It will use SecurityConfig bean UserDetailsService and get Users details e.g User Mayank and Durgesh

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationManager manager ;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper helper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        this.doAuthenticate(request.getEmail(), request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.helper.generateToken(userDetails);

        UserDTO userDTO = modelMapper.map(userDetails, UserDTO.class);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .user(userDTO).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);
        }catch (BadCredentialsException ex) {
            throw new BadApiRequest("Invalid Username or password exception !!");
        }
    }

    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        String name = principal.getName();
        return new ResponseEntity<>(modelMapper.map(userDetailsService.loadUserByUsername(name), UserDTO.class),HttpStatus.OK);
    }

}
