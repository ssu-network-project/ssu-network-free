package com.example.domain.child.api;

import com.example.domain.child.dto.PostChildReq;
import com.example.domain.child.dto.PostChildRes;
import com.example.domain.child.dto.RemainTimeRes;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.child.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/child")
public class childController {
    private final ChildService childService;

    /**각 도메인 별 API를 번호로 지정하였고, 관련 로직은 각 Service파일에 API에 맞는 번호로 구현하였습니다.**/
    //1.자녀회원가입 API
    @PostMapping("/sign-up")
    public PostChildRes signUp(@RequestBody PostChildReq postUserReq) {
        return childService.createUser(postUserReq);
    }

    //2.현재 받을 용돈 조회 API
    @GetMapping("/allowance/{userId}")
    public String getAllowance(@PathVariable(name="userId") Long userIdx){
        return "현재 받을 용돈은 "+ childService.getAllowance(userIdx)+"입니다";
    }

    //3.도메인 별 목표 시간 대비 남은 시간 조회 API
    @GetMapping("/remainTimeGoal/{userId}")
    public List<RemainTimeRes> getRemainTimeGoal(@PathVariable(name="userId") Long userIdx){
        return childService.getRemainTimeGoal(userIdx);
    }
}
