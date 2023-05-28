package com.example.domain.child.api;

import com.example.domain.child.dto.PostChildReq;
import com.example.domain.child.dto.PostChildRes;
import com.example.domain.child.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/child")
public class childController {
    private final ChildService childService;

    @PostMapping("/sign-up")
    public PostChildRes signUp(@RequestBody PostChildReq postUserReq) {
        return childService.createUser(postUserReq);
    }
}
