package com.salon.repository;

import com.salon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * USER REPOSITORY
 * 
 * WHAT IS A REPOSITORY?
 * A Repository is an INTERFACE (not a class!) that extends JpaRepository.
 * Spring Data JPA automatically creates the implementation at runtime.
 * 
 * JpaRepository<User, Long>:
 * - User = the entity type this repository manages
 * - Long = the type of the entity's primary key (id)
 * 
 * OUT-OF-THE-BOX METHODS (inherited from JpaRepository):
 * - save(user) → INSERT or UPDATE
 * - findById(id) → SELECT by primary key
 * - findAll() → SELECT all
 * - deleteById(id) → DELETE by primary key
 * - count() → COUNT(*)
 * 
 * CUSTOM QUERY METHODS:
 * Spring generates SQL from the method name!
 * findByEmail(email) → SELECT * FROM users WHERE email = ?
 * 
 * WHY Optional<User>?
 * Optional means "this might return a user, or it might not."
 * It forces us to handle the "not found" case, preventing NullPointerExceptions.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Find a user by their email address (for login) */
    Optional<User> findByEmail(String email);

    /** Find a user by phone number (for OTP login) */
    Optional<User> findByPhone(String phone);

    /** Check if an email is already registered */
    boolean existsByEmail(String email);

    /** Check if a phone number is already registered */
    boolean existsByPhone(String phone);
}
