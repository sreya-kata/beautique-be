package com.beautique.beautique.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users", schema = "app")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "name")
    private String name;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "pronouns")
    private String pronouns;

    @Column(name = "gender")
    private String gender;

    @Column(name = "age")
    private Integer age;
}
