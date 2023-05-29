package com.example.domain.child.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**자녀 스키마**/

/**
 * 회원 id
 * 이메일
 * 비밀번호
 * 초기 용돈
 */
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
    private Long allowance;

    @Builder
    public Child(Long id, String email, String password, Long allowance) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.allowance = allowance;
    }
}
