package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.AskingDto;
import com.example.musinsabackend.model.Asking;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.AskingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AskingService {

    @Autowired
    private AskingRepository askingRepository;

    public void createAsking(AskingDto askingDto, User user) {
        Asking asking = new Asking();
        asking.setUser(user); // 사용자 연결
        asking.setTitle(askingDto.getTitle());
        asking.setContent(askingDto.getContent());
        asking.setInquiryDate(LocalDateTime.now());
        askingRepository.save(asking);
    }

    public List<AskingDto> getUserAskings(String username) {
        List<Asking> askings = askingRepository.findByUserUsername(username); // username 기반 검색
        return askings.stream().map(asking -> {
            AskingDto dto = new AskingDto();
            dto.setId(asking.getId());
            dto.setTitle(asking.getTitle());
            dto.setContent(asking.getContent());
            return dto;
        }).collect(Collectors.toList());
    }
}
