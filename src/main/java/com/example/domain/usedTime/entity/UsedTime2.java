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
    private String domainName;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private Child child;

    @Builder
    public UsedTime2(Long id, Long usedTime, String domainName, Child child){
        this.id = id;
        this.usedTime = usedTime;
        this.domainName = domainName;
        this.child = child;
    }
}