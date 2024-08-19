package ttakkeun.ttakkeun_server.dto.diagnose;

public record ChatGPTCompletionDTO(String role, String content) {
    // role - 역할 (사용자인지 gpt인지)
    // content - 메세지 내용
}