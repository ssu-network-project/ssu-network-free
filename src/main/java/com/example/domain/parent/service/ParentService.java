package com.example.domain.parent.service;


import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.parent.dto.PostParentReq;
import com.example.domain.parent.entity.Parent;
import com.example.domain.parent.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    public String createUser(PostParentReq postParentReq){
        String childEmail = postParentReq.getChildEmail();
        System.out.println("childEmail = " + childEmail);
        Child child = childRepository.findByEmail(childEmail).get();
        Parent parent = postParentReq.toEntity(child);
        parentRepository.save(parent);
        return "success";
    }
}
