package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.repository.UserRepository;
import com.example.musinsabackend.model.User;
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
}