package com.salon.entity;

import com.salon.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * USER ENTITY - Maps to the "users" table in MySQL.
 * 
 * WHY "users" AND NOT "user"?
 * "user" is a reserved keyword in MySQL, so we name the table "users" to avoid conflicts.
 * 
 * WHAT EACH ANNOTATION DOES:
 * @Entity - Tells JPA "this class represents a database table"
 * @Table(name = "users") - Specifies the actual table name in MySQL
 * @Getter/@Setter - Lombok auto-generates getXxx()/setXxx() methods at compile time
 * @NoArgsConstructor - Lombok generates a no-argument constructor (required by JPA)
 * @AllArgsConstructor - Lombok generates a constructor with all fields
 * @Builder - Lets us create User objects with a clean builder pattern:
 *   User.builder().fullName("John").email("john@example.com").build()
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * @Id - Marks this field as the PRIMARY KEY
     * @GeneratedValue(IDENTITY) - MySQL auto-increments this value (1, 2, 3, ...)
     * 
     * Why Long and not int? Long supports larger numbers and is the JPA convention.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column(nullable = false) - This column CANNOT be null in the database.
     * If someone tries to save a User without a name, MySQL will reject it.
     */
    @Column(nullable = false)
    private String fullName;

    /**
     * unique = true: No two users can have the same email.
     * This is enforced at the DATABASE level, not just in Java code.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The password is stored as a BCrypt hash, NEVER as plain text.
     * BCrypt: "password123" → "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
     * Even if someone gets the database, they can't read the passwords.
     */
    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String phone;

    /**
     * @Enumerated(STRING) - Stores the enum as a STRING in MySQL ("CUSTOMER", "ADMIN")
     * instead of a number (0, 1, 2). This is more readable in the database.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String address;

    /** FCM token for push notifications */
    private String fcmToken;

    /** Profile image URL */
    @Column(length = 500)
    private String profileImageUrl;

    /**
     * Automatically set to the current timestamp when the entity is first saved.
     * @PrePersist runs BEFORE the entity is inserted into the database.
     */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
