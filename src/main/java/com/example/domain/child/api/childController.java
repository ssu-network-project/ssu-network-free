package com.example.domain.child.api;

import com.example.domain.child.dto.PostChildReq;
import com.example.domain.child.dto.PostChildRes;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.child.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/child")
public class childController {
    private final ChildService childService;
    private final ChildRepository childRepository;
    @PostMapping("/sign-up")
    public PostChildRes signUp(@RequestBody PostChildReq postUserReq) {
        return childService.createUser(postUserReq);
    }
}
