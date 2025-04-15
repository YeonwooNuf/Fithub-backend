package com.example.musinsabackend.service.user;

import com.example.musinsabackend.dto.KakaoUserInfoDto;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import com.example.musinsabackend.model.user.AuthProvider;
import com.example.musinsabackend.model.user.Role;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public String loginWithKakao(String code) {
        String accessToken = getAccessToken(code);
        KakaoUserInfoDto userInfo = getUserInfo(accessToken);

        String kakaoId = String.valueOf(userInfo.getId());
        Optional<User> optionalUser = userRepository.findByProviderAndProviderId(AuthProvider.KAKAO, kakaoId);
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();

            // ✅ 카카오에서 받은 프로필 이미지가 기존과 다르면 업데이트
            String latestProfileImage = userInfo.getKakao_account().getProfile().getProfile_image_url();
            if (latestProfileImage != null && !latestProfileImage.equals(user.getProfileImageUrl())) {
                user.setProfileImageUrl(latestProfileImage);
                userRepository.save(user); // 변경 사항 저장
            }

        } else {
            user = new User();
            user.setUsername("kakao_" + kakaoId);
            user.setNickname(userInfo.getKakao_account().getProfile().getNickname());
            user.setProfileImageUrl(userInfo.getKakao_account().getProfile().getProfile_image_url());
            user.setProvider(AuthProvider.KAKAO);
            user.setProviderId(kakaoId);
            user.setRole(Role.USER);
            user.setPassword("");  // 소셜 로그인은 비밀번호 사용 안함
            userRepository.save(user);
        }

        return jwtTokenProvider.generateToken(user);
    }

    private String getAccessToken(String code) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);
        return (String) response.getBody().get("access_token");
    }

    private KakaoUserInfoDto getUserInfo(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KakaoUserInfoDto> response = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, request, KakaoUserInfoDto.class);

        return response.getBody();
    }
}
