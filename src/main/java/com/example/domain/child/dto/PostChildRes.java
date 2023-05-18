package com.example.domain.child.dto;

import com.example.domain.child.entity.Child;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class PostChildRes {

  private Long id;

  private String successMessage;

  public static PostChildRes of(Child child){
     return PostChildRes.builder()
             .id(child.getId())
             .successMessage("회원가입 완료되었습니다")
             .build();
  }

}
