package com.example.domain.usedTime.repository;

import com.example.domain.child.entity.Child;
import com.example.domain.usedTime.entity.UsedTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsedTimeRepository extends JpaRepository<UsedTime,Long> {
    Optional<List<UsedTime>> findByChild(Child child);

    Long findByIpAddress(String ipAddress);
}
