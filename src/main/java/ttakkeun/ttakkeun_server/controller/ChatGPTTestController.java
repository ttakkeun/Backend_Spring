package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.dto.diagnose.ChatGPTRequestDTO;
import ttakkeun.ttakkeun_server.service.DiagnoseService.UseChatGPTService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ChatGPT")
public class ChatGPTTestController {

    @Autowired
    private UseChatGPTService useChatGPTService;

    // 사용 가능한 ChatGPT 모델 확인
    @Operation(summary = "사용 가능한 ChatGPT 모델 확인")
    @GetMapping("/modelList")
    public ResponseEntity<List<Map<String, Object>>> selectModelList() {
        List<Map<String, Object>> result = useChatGPTService.modelList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 사용하려는 ChatGPT 모델이 유효한 모델인지 확인
    @Operation(summary = "유효한 ChatGPT 모델인지 확인")
    @GetMapping("/model")
    public ResponseEntity<Map<String, Object>> isValidModel(@RequestParam(name = "modelName") String modelName) {
        Map<String, Object> result = useChatGPTService.isValidModel(modelName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // GPT 명령어 직접 수행, 테스트용
    @Operation(summary = "ChatGPT 통신 테스트용")
    @PostMapping("/prompt")
    public ResponseEntity<Map<String, Object>> selectPrompt(@RequestBody ChatGPTRequestDTO chatGPTRequestDTO) {
        log.debug("param :: " + chatGPTRequestDTO.toString());
        Map<String, Object> result = useChatGPTService.prompt(chatGPTRequestDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
