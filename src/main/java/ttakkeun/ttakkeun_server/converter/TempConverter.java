package ttakkeun.ttakkeun_server.converter;

import ttakkeun.ttakkeun_server.dto.TempResponse;

public class TempConverter {

    public static TempResponse.TempTestDto toTempTestDTO() {
        return TempResponse.TempTestDto.builder()
                .testString("테스트")
                .build();
    }

    public static TempResponse.TempExceptionDTO toTempExceptionDTO(Integer flag) {
        return TempResponse.TempExceptionDTO.builder()
                .flag(flag)
                .build();
    }
}
