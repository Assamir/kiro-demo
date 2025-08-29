package com.insurance.backoffice.interfaces.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/debug")
public class PasswordController {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/generate-hash")
    public String generateHash(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }
    
    @PostMapping("/verify-hash")
    public boolean verifyHash(@RequestParam String password, @RequestParam String hash) {
        return passwordEncoder.matches(password, hash);
    }
}