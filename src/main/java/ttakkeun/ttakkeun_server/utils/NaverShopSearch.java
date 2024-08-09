package ttakkeun.ttakkeun_server.utils;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class NaverShopSearch {
    public String search(String keyword) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", "2k1Nw65mLqVXlJNKkaE4");
        headers.add("X-Naver-Client-Secret", "p36CPPTMyN");

        String url = "https://openapi.naver.com/v1/search/shop.json?"
                + "query=" + keyword;

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = rest.exchange(
                    url, HttpMethod.GET, requestEntity, String.class);

            HttpStatusCode httpStatusCode = responseEntity.getStatusCode();
            int status = httpStatusCode.value();
            String response = responseEntity.getBody();

            System.out.println("Response status: " + status);
            System.out.println("Response body: " + response);

            return response;
        } catch (Exception e) {
            System.out.println("Error during API call: " + e.getMessage());
            return null;
        }
    }
}

