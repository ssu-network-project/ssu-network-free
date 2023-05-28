package com.example.domain.child.dto;

import com.example.domain.child.entity.Child;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**회원가입 API의 Request Body**/

/**
 * 이메일
 * 비밀번호
 * 초기 용돈
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostChildReq {
    private String email;

    private String password;

    private Long allowance;

    @Builder
    public PostChildReq(String email, String password, Long allowance){
        this.email = email;
        this.password = password;
        this.allowance = allowance;
    }

    public Child toEntity(){
        return Child.builder()
                .email(email)
                .password(password)
                .allowance(allowance)
                .build();
    }
}
