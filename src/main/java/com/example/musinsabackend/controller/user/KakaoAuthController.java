package com.example.musinsabackend.controller.user;

import com.example.musinsabackend.service.user.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/oauth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @GetMapping("/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        String token = kakaoAuthService.loginWithKakao(code);

        // ✅ 프론트로 리다이렉트 (토큰을 URL에 포함)
        String redirectUrl = "http://localhost:3000/oauth?token=" + token;
        return ResponseEntity.status(302)
                .location(URI.create(redirectUrl))
                .build();
    }
}
