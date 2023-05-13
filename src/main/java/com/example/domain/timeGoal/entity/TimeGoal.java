package com.example.domain.timeGoal.entity;


import com.example.domain.child.entity.Child;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String domainName;

    @Column
    Long time;

    @ManyToOne
    @JoinColumn(name="child_id")
    Child child;

    @Builder
    public TimeGoal(Long id, String domainName, Long time, Child child){
        this.id = id;
        this.domainName =domainName;
        this.time = time;
        this.child = child;
    }
}
