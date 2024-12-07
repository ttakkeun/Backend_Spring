package ttakkeun.ttakkeun_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponseDto {
    private Long memberId;
    private String username;
    private String email;
}
