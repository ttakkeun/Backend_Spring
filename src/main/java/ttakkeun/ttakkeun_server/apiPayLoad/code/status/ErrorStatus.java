package ttakkeun.ttakkeun_server.apiPayLoad.code.status;

import ttakkeun.ttakkeun_server.apiPayLoad.code.BaseErrorCode;
import ttakkeun.ttakkeun_server.apiPayLoad.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    //일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    //테스트 응답
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "this is test"),

    //JWT 토큰 에러
    TOKEN_EMPTY(HttpStatus.BAD_REQUEST, "TOKEN400", "토큰값이 존재하지 않습니다."),
    ACCESS_TOKEN_UNAUTHORIZED(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE, "TOKEN401", "유효하지 않은 Access Token입니다."),
    REFRESH_TOKEN_UNAUTHORIZED(HttpStatus.I_AM_A_TEAPOT, "TOKEN402", "유효하지 않은 Refresh Token입니다. 다시 로그인하세요."),
    INVALID_APPLE_ID_TOKEN(HttpStatus.UNAUTHORIZED,"TOKEN403", "Apple OAuth Identity Token 값이 올바르지 않습니다."),
    INVALID_APPLE_ID_TOKEN_INFO(HttpStatus.UNAUTHORIZED,"TOKEN404", "Apple id_token 값의 alg, kid 정보가 올바르지 않습니다."),
    APPLE_ID_TOKEN_EMPTY(HttpStatus.BAD_REQUEST,"TOKEN405", "ID TOKEN값이 존재하지 않습니다."),

    //Member 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "해당하는 사용자를 찾을 수 없습니다."),
    MEMBER_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "MEMBER4002", "등록된 사용자가 아닙니다."),

    //Pet 에러
    PET_NOT_FOUND(HttpStatus.BAD_REQUEST, "PET4001", "해당 사용자의 반려동물이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }

}
