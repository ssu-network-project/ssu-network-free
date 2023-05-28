package com.example.domain.timeGoal.repository;

import com.example.domain.child.entity.Child;
import com.example.domain.timeGoal.entity.TimeGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimeGoalRepository extends JpaRepository<TimeGoal, Long> {
    Optional<List<TimeGoal>> findByChild(Child child);
}
