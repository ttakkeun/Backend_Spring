package ttakkeun.ttakkeun_server.service.DiagnoseService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.config.ChatGPTConfig;
import org.springframework.beans.factory.annotation.Value;
import ttakkeun.ttakkeun_server.dto.diagnose.ChatGPTCompletionDTO;
import ttakkeun.ttakkeun_server.dto.diagnose.ChatGPTRequestDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@Slf4j
public class UseChatGPTService {

    private final ChatGPTConfig chatGPTConfig;

    public UseChatGPTService(ChatGPTConfig chatGPTConfig) {
        this.chatGPTConfig = chatGPTConfig;
    }

    @Value("${openai.url.model}")
    private String modelUrl;

    @Value("${openai.url.model-list}")
    private String modelListUrl;

    @Value("${openai.url.prompt}")
    private String promptUrl;

    // 사용 가능한 모델 리스트 조회
    public List<Map<String, Object>> modelList() {

        List<Map<String, Object>> resultList = null;

        // 토큰 정보가 포함된 Header를 가져옴
        HttpHeaders headers = chatGPTConfig.httpHeaders();

        // 통신을 위한 RestTemplate을 구성함
        ResponseEntity<String> response = chatGPTConfig
                .restTemplate()
                .exchange(modelUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        try {
            // 응답값을 가져옴
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> data = om.readValue(response.getBody(), new TypeReference<>() {
            });

            // 응답값을 결과에 넣은 후 로크 출력
            resultList = (List<Map<String, Object>>) data.get("data");
            for (Map<String, Object> object : resultList) {
                log.debug("ID: " + object.get("id"));
                log.debug("Object: " + object.get("object"));
                log.debug("Created: " + object.get("created"));
                log.debug("Owned By: " + object.get("owned_by"));
            }
        } catch (JsonMappingException e) {
            log.debug("JsonMappingException :: " + e.getMessage());
        } catch (JsonProcessingException e) {
            log.debug("JsonProcessingException :: " + e.getMessage());
        } catch (RuntimeException e) {
            log.debug("RuntimeException :: " + e.getMessage());
        }
        return resultList;
    }

    // 유효한 모델인지 확인
    public Map<String, Object> isValidModel(String modelName) {
        Map<String, Object> result = new HashMap<>();

        // 토큰 정보가 포함된 Header를 가져옴
        HttpHeaders headers = chatGPTConfig.httpHeaders();

        // 통신을 위한 RestTemplate 구성
        ResponseEntity<String> response = chatGPTConfig
                .restTemplate()
                .exchange(modelListUrl + "/" + modelName, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        try {
            // 응답값을 가져옴
            ObjectMapper om = new ObjectMapper();
            result = om.readValue(response.getBody(), new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.debug("JsonMappingException :: " + e.getMessage());
        } catch (RuntimeException e) {
            log.debug("RuntimeException :: " + e.getMessage());
        }
        return result;
    }

    // ChatGPT 통신 테스트용
    public Map<String, Object> prompt(ChatGPTRequestDTO chatGPTRequestDTO) {
        try {

            Map<String, Object> resultMap = new HashMap<>();

            // 토큰 정보가 포함된 Header를 가져옴
            HttpHeaders headers = chatGPTConfig.httpHeaders();

            // 통신을 위한 RestTemplate 구성
            HttpEntity<ChatGPTRequestDTO> requestEntity = new HttpEntity<>(chatGPTRequestDTO, headers);
            ResponseEntity<String> response = chatGPTConfig
                    .restTemplate()
                    .exchange(promptUrl, HttpMethod.POST, requestEntity, String.class);
            try {
                // String -> HashMap 역직렬화 구성
                ObjectMapper om = new ObjectMapper();
                resultMap = om.readValue(response.getBody(), new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                log.debug("JsonMappingException :: " + e.getMessage());
            } catch (RuntimeException e) {
                log.debug("RuntimeException :: " + e.getMessage());
            }
            return resultMap;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("에러 발생");
        }
    }
}
