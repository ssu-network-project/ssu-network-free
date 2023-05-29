package com.example.domain.child.repository;

import com.example.domain.child.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *자녀 등록 저장소
 */
public interface ChildRepository extends JpaRepository<Child,Long>{
    Optional<Child> findByEmail(String email);
}
