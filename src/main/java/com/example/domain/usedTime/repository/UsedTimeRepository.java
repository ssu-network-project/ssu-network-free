package com.example.domain.usedTime.repository;

import com.example.domain.child.entity.Child;
import com.example.domain.usedTime.entity.UsedTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsedTimeRepository extends JpaRepository<UsedTime,Long> {
    List<UsedTime> findByChild(Child child);

    Long findByIpAddress(String ipAddress);
}
