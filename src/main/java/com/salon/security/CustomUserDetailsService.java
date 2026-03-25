package com.salon.security;

import com.salon.entity.User;
import com.salon.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * CUSTOM USER DETAILS SERVICE
 * 
 * Spring Security needs a way to load user data during authentication.
 * This class implements UserDetailsService, which has one method: loadUserByUsername().
 * 
 * Spring Security calls this AUTOMATICALLY during:
 * 1. Login — to verify the user exists and get their password hash
 * 2. JWT Filter — to load user details when validating a token
 * 
 * WHY "username" WHEN WE USE EMAIL?
 * Spring Security's interface uses "username" as a generic term for the login identifier.
 * In our case, the "username" IS the email. We just implement it to look up by email.
 * 
 * WHAT IS UserDetails?
 * It's Spring Security's representation of an authenticated user.
 * Contains: username, password, authorities (roles/permissions), account status.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user from the database by email.
     * 
     * Returns a Spring Security UserDetails object that contains:
     * - Username (email)
     * - Hashed password
     * - Authorities (roles): We prefix with "ROLE_" as per Spring Security convention
     *   "CUSTOMER" → "ROLE_CUSTOMER"
     *   This allows us to use @PreAuthorize("hasRole('CUSTOMER')") on controllers.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
