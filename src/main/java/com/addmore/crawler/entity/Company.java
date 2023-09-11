package com.addmore.crawler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "company")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "hibernate_sequence")
    private Long id;

    @Column(length = 30)
    private String code;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String tel;

    private String addr;

    private String roadAddr;

    private String homePage;

    @Column(length = 100)
    private String category;

    private boolean is114;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate regDate;

    private String memo;

    private String detailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private TargetStatus status;

    @Enumerated(EnumType.STRING)
    private EMapType mapType;
}