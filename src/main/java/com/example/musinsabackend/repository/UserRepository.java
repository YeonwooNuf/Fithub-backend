package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.model.user.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
