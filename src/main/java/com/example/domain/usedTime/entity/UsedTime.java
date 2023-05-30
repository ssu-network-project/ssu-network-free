package com.example.domain.usedTime.entity;


import com.example.domain.child.entity.Child;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**도메인별 목표 시간 스키마**/

/**
 * 사용시간 id
 * 사용시간 (단위 : 밀리세컨드)
 * 처음 캡쳐된 시간 (단위 : 밀리세컨드)
 * 도메인
 * 추출된 IP 주소
 * 캡쳐된 패킷 수
 * 등록 자녀 id
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class UsedTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long usedTime;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;

    @Column
    private String domainName;

    @Builder
    public UsedTime(Long id, Long usedTime, String domainName, Child child){
        this.id = id;
        this.usedTime = usedTime;
        this.domainName = domainName;
        this.child = child;
    }
}