package com.safeqr.app.user.controller;

import com.safeqr.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1")
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping(value = "/version", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> version() {
        // System.out.println(userService.getUserByEmail());
        System.out.println("Health Check");
        return ResponseEntity.ok(Map.of("version","SafeQR v1.0.2"))
    }

}
