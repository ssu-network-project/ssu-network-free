package com.example.domain.child.repository;

import com.example.domain.child.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child,Long>{
    Optional<Child> findByEmail(String email);
}
