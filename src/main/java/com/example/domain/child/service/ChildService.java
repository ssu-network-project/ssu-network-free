package com.example.domain.child.service;

import com.example.domain.child.dto.PostChildReq;
import com.example.domain.child.dto.PostChildRes;
import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildService {
    private final ChildRepository childRepository;

    public PostChildRes createUser(PostChildReq postUserReq){
        Child child = postUserReq.toEntity();
        childRepository.save(child);
        return PostChildRes.of(child);
    }
}
