package com.taller.msvc_security.Repository;

import com.taller.msvc_security.Entities.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findByToken(String token);

    List<PasswordResetToken> findByUserId(String userId);

    void deleteByToken(String token);

    void deleteByUserId(String userId);

    List<PasswordResetToken> findByExpiryDateBefore(LocalDateTime dateTime);

    void deleteByExpiryDateBefore(LocalDateTime dateTime);

    boolean existsByToken(String token);

    boolean existsByUserIdAndExpiryDateAfter(String userId, LocalDateTime dateTime);
}