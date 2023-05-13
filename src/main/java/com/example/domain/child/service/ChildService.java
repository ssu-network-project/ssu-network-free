package com.example.domain.child.service;

import com.example.domain.child.dto.PostChildReq;
import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.parent.entity.Parent;
import com.example.domain.parent.repository.ParentRepository;
import com.example.global.JPacketCapture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ChildService {
    private final ChildRepository childRepository;
    private final JPacketCapture jPacketCapture;
    public String createUser(PostChildReq postUserReq){
        Child child = postUserReq.toEntity();
        childRepository.save(child);
        return "success";
    }
}
