package com.addmore.crawler.service;


import com.addmore.crawler.entity.Company;
import com.addmore.crawler.entity.EMapType;
import com.addmore.crawler.entity.ETargetStatus;
import com.addmore.crawler.entity.TargetStatus;
import com.addmore.crawler.repository.CrawlerRepository;
import com.addmore.crawler.repository.StatusRepository;
import com.addmore.crawler.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class CrawlerServiceImpl implements CrawlerService {

    private CrawlerRepository crawlerRepository;
    private StatusRepository statusRepository;
    @Autowired
    public CrawlerServiceImpl (CrawlerRepository crawlerRepository, StatusRepository statusRepository) {
        this.crawlerRepository = crawlerRepository;
        this.statusRepository = statusRepository;
    }


    private final String NAVER_MAP_HEADER_ADDR = "https://map.naver.com/v5/api/search?query=";

    private final String NAVER_MAP_TAIL_ADDR = "&page=1&displayCount=300";

    public List<Map<String, Object>> getDataFromWeb(String keyword) {
        List<Map<String, Object>> list = null;
        try {
            String fullNaverAddress = "https://map.naver.com/v5/api/search?query=" + URLEncoder.encode(keyword)  + "&page=1&displayCount=300";
            URL url = new URL(fullNaverAddress);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows Phone 10.0; Android 4.2.1; Microsoft; Lumia 640 XL LTE) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Mobile Safari/537.36 Edge/12.10166");
            connection.setRequestProperty("Referer", "https://map.kakao.com");
            StringBuilder sb = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                log.info("사이트 연결 성공");
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line = "";
                String responseData = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                responseData = sb.toString(); // 최종 데이터 저장
                Map<String, Object> result_pageSource = (Map<String, Object>) CommonUtil.fromJson(responseData).get("result");
                Map<String, Object> placeInfo = (Map<String, Object>)result_pageSource.get("place");
                if (placeInfo == null)
                    return new ArrayList<>();
                list = (List<Map<String, Object>>)placeInfo.get("list");
                for (Map<String, Object> data : list) {
                    log.info(data.toString());
                    String tel = (String)data.get("tel");
                    if (tel.indexOf("070") != -1)
                        continue;
                    Thread.sleep(1000L);
                    String id = (String)data.get("id");
                    /*this.driver.get("https://m.map.naver.com/search2/site.nhn?style=v5&code=" + id);
                    boolean is114 = !(this.driver.getPageSource().indexOf("02-114") == -1);*
                     */
                    //boolean is114 = false;
                    data.put("is114", false);
                    int result = insertToNcd(data);
                    if (result != 1)
                        log.error(String.valueOf(data.get("id").toString()) + " : 위 아이디의 네이버 데이터 삽입/수정에 실패하였습니다.");
                }
                return list;
            }

            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public int insertToNcd(Map<String, Object> data) {
            int result = 0;
            try {
                Company c = this.crawlerRepository.findByCode((String)data.get("id"));
                if (c != null) {
                    Optional<Company> target = this.crawlerRepository.findById(c.getId());
                    c = target.get();
                } else {
                    c = new Company();
                    c.setCreatedDate(LocalDate.now());
                    TargetStatus ts = this.statusRepository.findByName(ETargetStatus.NOT_CALLED);
                    c.setStatus(ts);
                }
                c.setCode((String)data.get("id"));
                c.setName((String)data.get("name"));
                c.setAddr((String)data.get("address"));
                c.setRoadAddr((String)data.get("roadAddress"));
                c.setTel((String)data.get("tel"));
                c.setDetailUrl("https://map.naver.com/v5/entry/place/" + (String)data.get("id"));
                c.setMapType(EMapType.NAVER_MAP);
                List<String> category = (List<String>)data.get("category");
                c.setCategory(String.join(",", (Iterable)category));
                if (data.get("homePage") != null)
                    c.setHomePage((String)data.get("homePage"));
                if (data.get("thumUrl") != null && data.get("thumUrl") != "") {
                    String thumUrl = (String)data.get("thumUrl");
                    String[] thumUrlArr = new String[1];
                    if (thumUrl.indexOf("ldb.phinf.naver.net") != -1) {
                        thumUrlArr = thumUrl.split("http://ldb.phinf.naver.net/");
                    } else if (thumUrl.indexOf("ldb-phinf.pstatic.net") != -1) {
                        thumUrlArr = thumUrl.split("https://ldb-phinf.pstatic.net/");
                    } else if (thumUrl.indexOf("blogfiles.naver.net") != -1) {
                        thumUrlArr = thumUrl.split("http://blogfiles.naver.net/");
                    }
                    String regDate = thumUrlArr[1].substring(0, 8);
                    c.setRegDate(LocalDate.of(Integer.parseInt(regDate.substring(0, 4)), Integer.parseInt(regDate.substring(4, 6)), Integer.parseInt(regDate.substring(6, 8))));
                }
                c.set114(((Boolean)data.get("is114")).booleanValue());
                this.crawlerRepository.save(c);
                result = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
}