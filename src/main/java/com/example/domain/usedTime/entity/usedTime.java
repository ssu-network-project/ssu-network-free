package com.example.domain.usedTime.entity;


import com.example.domain.child.entity.Child;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class UsedTime {

    @Id
    private Long id;

    @Column
    private Long usedTime;

    @Column
    private String domainName;

    @Column
    private String ipAddress;

    @Column
    private Long weight;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;

    @Builder
    public UsedTime(Long id, Long usedTime, String domainName, String ipAddress, Long weight, Child child){
        this.id = id;
        this.usedTime = usedTime;
        this.domainName = domainName;
        this.ipAddress = ipAddress;
        this.weight = weight;
        this.child = child;
    }
}
