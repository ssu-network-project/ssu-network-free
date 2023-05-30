package com.example.domain.usedTime.repository;

import com.example.domain.child.entity.Child;
import com.example.domain.usedTime.entity.UsedTime2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsedTimeRepository2 extends JpaRepository<UsedTime2,Long> {
    Optional<List<UsedTime2>> findByChild(Child child);

    Long findByIpAddress(String ipAddress);
}
