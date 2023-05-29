package com.example.domain.timeGoal.entity;


import com.example.domain.child.entity.Child;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**도메인별 목표 시간 스키마**/

/**
 * 목표 id
 * 도메인
 * 시간 (단위 :분)
 * 등록한 자녀 id
 */
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
