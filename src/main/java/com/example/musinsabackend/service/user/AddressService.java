package com.example.musinsabackend.service.user;

import com.example.musinsabackend.dto.AddressDto;
import com.example.musinsabackend.model.Address;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.user.AddressRepository;
import com.example.musinsabackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    // ✅ 특정 사용자의 주소 목록 조회
    public List<AddressDto> getUserAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUser_UserId(userId);
        return addresses.stream().map(AddressDto::new).collect(Collectors.toList());
    }

    // ✅ 새로운 주소 추가
    @Transactional
    public AddressDto addAddress(Long userId, AddressDto addressDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Address address = new Address(user, addressDto.getZonecode(), addressDto.getRoadAddress(),
                addressDto.getJibunAddress(), addressDto.getDetailAddress(), addressDto.getReference());

        addressRepository.save(address);
        return new AddressDto(address);
    }

    // ✅ 기존 주소 수정
    @Transactional
    public AddressDto updateAddress(Long userId, Long addressId, AddressDto addressDto) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 주소를 찾을 수 없습니다."));

        // ✅ 주소의 소유자가 맞는지 확인
        if (!address.getUser().getUserId().equals(userId)) {
            throw new SecurityException("❌ 해당 주소를 수정할 권한이 없습니다.");
        }

        // ✅ 주소 정보 업데이트
        address.setZonecode(addressDto.getZonecode());
        address.setRoadAddress(addressDto.getRoadAddress());
        address.setJibunAddress(addressDto.getJibunAddress());
        address.setDetailAddress(addressDto.getDetailAddress());
        address.setReference(addressDto.getReference());

        addressRepository.save(address);

        return new AddressDto(address);
    }

    // ✅ 주소 삭제
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 주소를 찾을 수 없습니다."));

        if (!address.getUser().getUserId().equals(userId)) {
            throw new SecurityException("❌ 해당 주소를 삭제할 권한이 없습니다.");
        }

        addressRepository.delete(address);
    }

    public void setDefaultAddress(Long userId, Long addressId) {
        // ✅ 해당 사용자의 모든 주소에서 기본 배송지 해제
        List<Address> userAddresses = addressRepository.findByUser_UserId(userId);
        for (Address addr : userAddresses) {
            addr.setDefault(false);
        }

        // ✅ 새로운 기본 배송지 설정
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 해당 주소를 찾을 수 없습니다."));

        if (!address.getUser().getUserId().equals(userId)) {
            throw new SecurityException("❌ 해당 주소를 기본 배송지로 설정할 권한이 없습니다.");
        }

        address.setDefault(true);
        addressRepository.saveAll(userAddresses); // ✅ 기존 주소 정보 업데이트
        addressRepository.save(address); // ✅ 새로운 기본 주소 저장
    }
}
