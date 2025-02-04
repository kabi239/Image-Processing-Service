package com.ImageProcessing.Image_Processing_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ImageProcessing.Image_Processing_Service.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
