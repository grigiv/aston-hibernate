package ru.astondevs.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    @Id
    @Column(name = "user_id")
    private UUID id;
    private String name;
    private String email;
    private int age;
    @Column(name = "created_ts")
    private Instant createdAt;
}
