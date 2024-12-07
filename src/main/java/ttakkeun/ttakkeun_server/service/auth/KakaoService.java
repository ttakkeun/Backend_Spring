package ttakkeun.ttakkeun_server.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ttakkeun.ttakkeun_server.dto.auth.kakao.KakaoUserDTO;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;

    public KakaoUserDTO validateKakaoToken(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 카카오 API 호출
        ResponseEntity<KakaoUserDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, KakaoUserDTO.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("카카오 로그인 인증 실패");
        }
    }
}
