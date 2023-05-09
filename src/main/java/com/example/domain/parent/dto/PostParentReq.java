package com.example.domain.parent.dto;

import com.example.domain.child.entity.Child;
import com.example.domain.parent.entity.Parent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostParentReq {

    private String email;
    private String password;
    private String childEmail;

    @Builder
    public PostParentReq(String email, String password, String childEmail){
        this.email = email;
        this.password = password;
        this.childEmail = childEmail;
    }

    public Parent toEntity(Child child){
        return Parent.builder()
                .email(email)
                .password(password)
                .child(child)
                .build();
    }
}
