package com.example.domain.parent.api;


import com.example.domain.child.dto.PostChildReq;
import com.example.domain.parent.dto.PostParentReq;
import com.example.domain.parent.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/parent")
public class ParentController {
    private final ParentService parentService;
    @PostMapping("/sign-up")
    public String signUp(@RequestBody PostParentReq postParentReq){
            parentService.createUser(postParentReq);
            return "Sign-up success";
    }
}
