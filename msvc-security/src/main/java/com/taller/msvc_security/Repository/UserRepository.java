package com.taller.msvc_security.Repository;

import com.taller.msvc_security.Entities.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserDocument, String> {

    // Find by username (for login and duplicate checks)
    Optional<UserDocument> findByUsername(String username);

    // Find by email (for password recovery and duplicate checks)
    Optional<UserDocument> findByEmail(String email);

    // Check if username exists (for registration validation)
    boolean existsByUsername(String username);

    // Check if email exists (for registration validation)
    boolean existsByEmail(String email);

    // Find by username or email (useful for login with either option)
    Optional<UserDocument> findByUsernameOrEmail(String username, String email);

    // Find enabled users (with pagination support)
    Page<UserDocument> findByEnabledTrue(Pageable pageable);

    // Find by custom criteria for filtering
    Page<UserDocument> findByFirstNameContainingOrLastNameContaining(
            String firstName, String lastName, Pageable pageable);

    // Find users by role
    Page<UserDocument> findByRolesContaining(String role, Pageable pageable);

    // Search users by any field
    Page<UserDocument> findByUsernameContainingOrEmailContainingOrFirstNameContainingOrLastNameContaining(
            String username, String email, String firstName, String lastName, Pageable pageable);
}