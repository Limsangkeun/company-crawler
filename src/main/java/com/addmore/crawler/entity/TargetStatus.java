package com.addmore.crawler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "target_status")
@Getter
@Setter
public class TargetStatus {
    @Id
    @Column(length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    private ETargetStatus name;

    public TargetStatus() {
        this.id = UUID.randomUUID().toString();
    }
}