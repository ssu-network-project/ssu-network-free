package com.example.domain.timeGoal.repository;

import com.example.domain.timeGoal.entity.TimeGoal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeGoalRepository extends JpaRepository<TimeGoal, Long> {
}
