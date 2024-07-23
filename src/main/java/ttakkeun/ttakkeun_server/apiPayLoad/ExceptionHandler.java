package ttakkeun.ttakkeun_server.apiPayLoad;

import ttakkeun.ttakkeun_server.apiPayLoad.code.BaseErrorCode;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.GeneralException;

public class ExceptionHandler extends GeneralException {
    public ExceptionHandler(BaseErrorCode code) {
        super(code);
    }
}
