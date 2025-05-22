package ru.astonddevs.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private UUID id;
    private String name;
    private String email;
    private int age;
    @Column(name = "created_ts")
    private Instant createdAt;
}
