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
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다. 로그인 정보를 확인해주세요."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청한 리소스를 찾을 수 없습니다"),

    //테스트 응답
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "this is test"),

    //JWT 토큰 에러
    TOKEN_EMPTY(HttpStatus.BAD_REQUEST, "TOKEN400", "토큰값이 존재하지 않습니다."),
    ACCESS_TOKEN_UNAUTHORIZED(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE, "TOKEN401", "유효하지 않은 Access Token입니다."),
    REFRESH_TOKEN_UNAUTHORIZED(HttpStatus.I_AM_A_TEAPOT, "TOKEN402", "유효하지 않은 RefreshToken입니다. 다시 로그인하세요."),
    INVALID_APPLE_ID_TOKEN(HttpStatus.UNAUTHORIZED,"TOKEN403", "Apple OAuth Identity Token 값이 올바르지 않습니다."),
    INVALID_APPLE_ID_TOKEN_INFO(HttpStatus.UNAUTHORIZED,"TOKEN404", "Apple id_token 값의 alg, kid 정보가 올바르지 않습니다."),
    APPLE_ID_TOKEN_EMPTY(HttpStatus.BAD_REQUEST,"TOKEN405", "ID TOKEN값이 존재하지 않습니다."),

    //Member 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "해당하는 사용자를 찾을 수 없습니다."),
    MEMBER_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "MEMBER4002", "등록된 사용자가 아닙니다."),
    MEMBER_NOT_HAVE_PET(HttpStatus.BAD_REQUEST, "MEMBER4003", "반려동물이 없는 사용자입니다."),
    MEMBER_ALREADY_LOGOUT(HttpStatus.BAD_REQUEST, "MEMBER4004", "이미 토큰이 만료된 사용자입니다."),


    //Record 에러
    RECORD_NOT_FOUND(HttpStatus.BAD_REQUEST, "RECORD4000", "해당하는 일지를 찾을 수 없습니다."),
    ANSWER_NOT_FOUND(HttpStatus.BAD_REQUEST, "RECORD4001", "해당하는 답변을 찾을 수 없습니다."),

    // Dignose 에러
    DIAGNOSE_NOT_FOUND(HttpStatus.BAD_REQUEST, "DIAGNOSE4001", "해당하는 진단 결과를 찾을 수 없습니다."),

    //Pet 에러
    PET_ID_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "PET4001", "유효하지 않은 Pet ID입니다."),
    PET_NOT_FOUND(HttpStatus.BAD_REQUEST, "PET4002", "해당 사용자의 반려동물이 아닙니다."),

    //이미지 에러
    IMAGE_EMPTY(HttpStatus.BAD_REQUEST, "IMAGE4000", "이미지가 첨부되지 않았습니다."),
    IMAGE_NOT_SAVE(HttpStatus.BAD_REQUEST, "IMAGE4001", "이미지 저장에 실패했습니다." ),

    //S3 에러
    S3_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, "S34000", "이미지 업로드에 실패하였습니다."),
    NO_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "S34001", "해당되는 파일 확장자가 없습니다."),
    INVALID_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "S34002", "유효하지 않은 파일 확장자입니다."),

    //Question 에러
    QUESTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "QUESTION4001", "유효하지 않은 Question ID입니다."),

    // point 에러
    MEMBER_HAS_NO_POINT(HttpStatus.NOT_FOUND, "POINT4001", "해당하는 멤버에게 포인트 값이 존재하지 않습니다. DB 관리자에게 문의해주세요"),

    // 투두 에러
    TODO_ID_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "TODO4000", "유효하지 않은 Todo ID입니다."),
    TODO_STATUS_IS_DONE(HttpStatus.BAD_REQUEST, "TODO4001", "투두 항목이 이미 완료된 상태입니다."),
    TODO_STATUS_IS_ONPROGRESS(HttpStatus.BAD_REQUEST, "TODO4002", "투두 항목이 완료되지 않았습니다."),

    // Tip 에러
    TIP_ID_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "TIP4000", "유효하지 않은 Tip ID입니다.");


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