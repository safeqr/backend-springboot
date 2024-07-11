package com.safeqr.app.user.service;

import com.safeqr.app.user.entity.UserEntity;
import com.safeqr.app.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String getUserByEmail() {

        // Retrieve the user by email
        UserEntity retrievedUser = userRepository.findByEmail("piggyinu@gmail.com");
        if (retrievedUser != null) {
            return "User found: " + retrievedUser.getName();
        }
        return "User not found";
    }
}
