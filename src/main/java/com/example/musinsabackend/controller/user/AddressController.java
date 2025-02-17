package com.example.musinsabackend.controller.user;

import com.example.musinsabackend.dto.AddressDto;
import com.example.musinsabackend.service.user.AddressService;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users") // ✅ `/api/users/addresses` 경로 설정
public class AddressController {

    private final AddressService addressService;
    private final JwtTokenProvider jwtTokenProvider; // ✅ JWT 토큰에서 userId 추출

    public AddressController(AddressService addressService, JwtTokenProvider jwtTokenProvider) {
        this.addressService = addressService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ✅ 사용자의 모든 주소 조회 (JWT에서 userId 추출)
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getUserAddresses(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        List<AddressDto> addresses = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    // ✅ 새로운 주소 추가
    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> addUserAddress(HttpServletRequest request, @RequestBody AddressDto addressDto) {
        Long userId = extractUserIdFromRequest(request);
        AddressDto newAddress = addressService.addAddress(userId, addressDto);
        return ResponseEntity.ok(newAddress);
    }

    // ✅ 기존 주소 수정
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateUserAddress(HttpServletRequest request,
                                                        @PathVariable Long addressId,
                                                        @RequestBody AddressDto addressDto) {
        Long userId = extractUserIdFromRequest(request);
        AddressDto updatedAddress = addressService.updateAddress(userId, addressId, addressDto);
        return ResponseEntity.ok(updatedAddress);
    }

    // ✅ 주소 삭제
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteUserAddress(HttpServletRequest request, @PathVariable Long addressId) {
        Long userId = extractUserIdFromRequest(request);
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    // ✅ 기본 배송지 설정
    @PutMapping("/addresses/{addressId}/set-default")
    public ResponseEntity<Void> setDefaultAddress(HttpServletRequest request, @PathVariable Long addressId) {
        Long userId = extractUserIdFromRequest(request);
        addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok().build();
    }

    // ✅ JWT에서 userId를 추출하는 메서드
    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        throw new IllegalArgumentException("❌ JWT 토큰이 제공되지 않았습니다.");
    }
}
