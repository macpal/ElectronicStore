package com.monks.electronic.store.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class HomeController {

    @RequestMapping("/welcome")
    public String test() {
        return "Welcome to my app!";
    }
}
