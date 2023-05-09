package com.example.domain.parent.entity;


import com.example.domain.child.entity.Child;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String password;

    @OneToOne
    @JoinColumn(name = "child_id")
    private Child child;

    @Builder
    public Parent(Long id, String email,String password, Child child){
        this.id = id;
        this.email = email;
        this.password = password;
        this.child = child;
    }
}
