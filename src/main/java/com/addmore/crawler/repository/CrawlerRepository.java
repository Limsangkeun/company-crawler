package com.addmore.crawler.repository;

import com.addmore.crawler.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrawlerRepository extends JpaRepository<Company, Long> {
    Company findByCode(String paramString);
}