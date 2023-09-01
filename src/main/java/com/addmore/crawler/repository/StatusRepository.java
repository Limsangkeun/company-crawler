package com.addmore.crawler.repository;

import com.addmore.crawler.entity.ETargetStatus;
import com.addmore.crawler.entity.TargetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<TargetStatus, String> {
    TargetStatus findByName(ETargetStatus paramETargetStatus);
}