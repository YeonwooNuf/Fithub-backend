package com.example.musinsabackend.service;

import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.UserRepository;
import com.example.musinsabackend.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setNickname(userDto.getNickname());
        user.setBirthdate(userDto.getBirthdate());
        user.setPhone(userDto.getPhone());
        user.setGender(userDto.getGender());
        userRepository.save(user);
    }

    public boolean validateUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getUsername()).orElse(null);

        // Check if user exists and password matches
        return user != null && user.getPassword().equals(userDto.getPassword());
    }

    public User findUserByUsername(String username) {
        return userRepository.findById(username).orElse(null);
    }

}
