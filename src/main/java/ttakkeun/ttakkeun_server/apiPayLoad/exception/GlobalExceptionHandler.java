package ttakkeun.ttakkeun_server.apiPayLoad.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ApiResponse<String>> handleHttpMediaTypeNotSupportedException(
		HttpMediaTypeNotSupportedException e) {
		logger.error("Unsupported Media Type: " + e.getMessage(), e);
		ApiResponse<String> response = ApiResponse.ofFailure(ErrorStatus.UNSUPPORTED_MEDIA_TYPE, "지원되지 않는 미디어 타입입니다.");
		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
	}
}