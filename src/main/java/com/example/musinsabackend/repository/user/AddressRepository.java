package com.example.musinsabackend.repository.user;

import com.example.musinsabackend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // ✅ 특정 사용자의 모든 주소 조회
    List<Address> findByUser_UserId(Long userId);
}
