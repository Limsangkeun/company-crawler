package com.addmore.crawler.service;
import java.util.List;
import java.util.Map;

public interface CrawlerService {
    List<Map<String, Object>> getDataFromWeb(String paramString);
}