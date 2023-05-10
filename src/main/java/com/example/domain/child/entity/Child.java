package com.example.domain.child.entity;

import com.example.domain.parent.entity.Parent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String password;

//    @OneToOne
//    @JoinColumn(name = "parent_id")
//    private Parent parent;

    @Column
    private Long score;

    @Column
    private Long usedTime;

    @Builder
    public Child(Long id, String email, String password, Long score, Long usedTime) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.score = score;
        this.usedTime=usedTime;
    }
}
