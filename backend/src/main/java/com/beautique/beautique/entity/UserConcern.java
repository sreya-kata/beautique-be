package com.beautique.beautique.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_concerns", schema = "app")
public class UserConcern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_concern_id")
    private Integer userConcernId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "concern_id")
    private Integer concernId;

    public UserConcern(Integer userId, Integer concernId) {
        this.userId = userId;
        this.concernId = concernId;
    }

    public UserConcern() {

    }
}
