package com.example.domain.child.dto;

import com.example.domain.child.entity.Child;
import com.example.domain.parent.entity.Parent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostChildReq {
    private String email;

    private String password;
    private String parentEmail;
    private Long score;

    private Long usedTime;
    @Builder
    public PostChildReq(String email, String password, Long score, Long usedTime){
        this.email = email;
        this.password = password;
        this.score = score;
        this.usedTime = usedTime;
    }

    public Child toEntity(){
        return Child.builder()
                .email(email)
                .password(password)
                .score(score)
                .usedTime(usedTime)
                .build();
    }
}
