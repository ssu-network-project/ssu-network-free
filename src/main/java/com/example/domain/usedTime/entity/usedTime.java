package com.example.domain.usedTime.entity;


import com.example.domain.child.entity.Child;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class usedTime {

    @Id
    private Long id;

    @Column
    private String domainName;

    @Column
    private String ipAddress;

    @Column
    private Long weight;

    @OneToOne
    @JoinColumn(name = "child_id")
    private Child child;

    @Builder
    public usedTime(Long id, String domainName, String ipAddress, Long weight, Child child){
        this.id = id;
        this.domainName = domainName;
        this.ipAddress = ipAddress;
        this.weight = weight;
        this.child = child;
    }
}
