package com.example.domain.child.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private Long score;

    @Builder
    public Child(Long id, String email, String password, Long score) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.score = score;
    }
}
