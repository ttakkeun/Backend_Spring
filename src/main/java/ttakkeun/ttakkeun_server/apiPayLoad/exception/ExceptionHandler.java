package ttakkeun.ttakkeun_server.apiPayLoad.exception;

import ttakkeun.ttakkeun_server.apiPayLoad.code.BaseErrorCode;

public class ExceptionHandler extends GeneralException {
    public ExceptionHandler(BaseErrorCode code) {
        super(code);
    }
}
