package com.example.domain.usedTime.entity;


import com.example.domain.child.entity.Child;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class UsedTime2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long usedTime;

    @Column
    private Long firstCapturedTime;

    @Column
    private String domainName;

    @Column
    private String ipAddress;

    @Column
    private int capturedNum;


    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;

    @Builder
    public UsedTime2(Long id, Long usedTime, Long firstCapturedTime, String domainName, String ipAddress, Child child, int capturedNum){
        this.id = id;
        this.usedTime = usedTime;
        this.firstCapturedTime = firstCapturedTime;
        this.domainName = domainName;
        this.ipAddress = ipAddress;
        this.capturedNum = capturedNum;
        this.child = child;
    }
}