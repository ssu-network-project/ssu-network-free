package com.example.domain.timeGoal.repository;

import com.example.domain.child.entity.Child;
import com.example.domain.timeGoal.entity.TimeGoal;
import com.example.domain.usedTime.entity.UsedTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.util.List;
import java.util.Optional;


/**
 *도메인별 목표 시간 등록 저장소
 */
public interface TimeGoalRepository extends JpaRepository<TimeGoal, Long> {
    Optional<List<TimeGoal>> findByChild(Child child);
}
