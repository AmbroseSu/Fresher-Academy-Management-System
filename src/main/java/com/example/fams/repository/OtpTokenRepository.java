package com.example.fams.repository;

import com.example.fams.entities.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    @Modifying
    @Query("DELETE FROM OtpToken o WHERE o.expirationTime < :expiryTime")
    void deleteExpiredTokens(LocalDateTime expiryTime);

    @Query("SELECT o FROM OtpToken o WHERE o.userId = :userId")
    OtpToken findByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
}
