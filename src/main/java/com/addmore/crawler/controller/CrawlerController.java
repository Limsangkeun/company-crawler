package com.addmore.crawler.controller;

import com.addmore.crawler.service.CrawlerService;
import com.addmore.crawler.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class CrawlerController {
    private CrawlerService service;

    @Autowired
    public CrawlerController(CrawlerService crawlerService) {
        this.service = crawlerService;
    }

    @RequestMapping({"/startCrawling"})
    public ResponseEntity<String> startCrawling(@RequestBody Map<String, String> data) {
        Map<String, Object> resultData = new HashMap<>();
        try {
            String keyword = data.get("keyword");
            List<Map<String, Object>> list = this.service.getDataFromWeb(keyword);
            resultData.put("list", list);
        } catch (NullPointerException ne) {
            log.error("parameter name 'data' is null");
            Map<String, Object> emptyMap = new HashMap<>();
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(emptyMap);
            resultData.put("list", list);
        }
        return new ResponseEntity(CommonUtil.toJson(resultData), HttpStatus.OK);
    }
}