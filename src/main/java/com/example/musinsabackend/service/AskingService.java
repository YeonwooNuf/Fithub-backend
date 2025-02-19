package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.AskingDto;
import com.example.musinsabackend.model.Asking;
import com.example.musinsabackend.model.AskingStatus;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.AskingRepository;
import com.example.musinsabackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AskingService {

    @Autowired
    private AskingRepository askingRepository;

    @Autowired
    private UserRepository userRepository;

    // 문의 생성
    public AskingDto createAsking(String username, AskingDto askingDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Asking asking = new Asking();
        asking.setUser(user);
        asking.setTitle(askingDto.getTitle());
        asking.setContent(askingDto.getContent());
        asking.setCreatedAt(new java.util.Date());
        asking.setUpdatedAt(new java.util.Date());
        asking.setStatus(AskingStatus.PENDING);

        Asking savedAsking = askingRepository.save(asking);

        return mapToDto(savedAsking);
    }

    // 사용자별 문의 조회
    public List<AskingDto> getUserAskings(String username) {
        List<Asking> askings = askingRepository.findByUser_Username(username);

        return askings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // 특정 문의 조회
    public AskingDto getAskingById(Long askingId) {
        Asking asking = askingRepository.findById(askingId)
                .orElseThrow(() -> new IllegalArgumentException("문의 내역을 찾을 수 없습니다."));

        return mapToDto(asking);
    }

    // DTO 매핑
    private AskingDto mapToDto(Asking asking) {
        AskingDto dto = new AskingDto();
        dto.setId(asking.getId());
        dto.setTitle(asking.getTitle());
        dto.setContent(asking.getContent());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dto.setCreatedAt(asking.getCreatedAt().toString());
        dto.setUpdatedAt(asking.getUpdatedAt().toString());
        dto.setStatus(asking.getStatus().name());

        return dto;
    }
}
