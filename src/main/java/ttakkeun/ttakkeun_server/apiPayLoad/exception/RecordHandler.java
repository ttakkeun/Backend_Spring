package ttakkeun.ttakkeun_server.apiPayLoad.exception;

import ttakkeun.ttakkeun_server.apiPayLoad.code.BaseErrorCode;

public class RecordHandler extends GeneralException {
    public RecordHandler(BaseErrorCode code) {
        super(code);
    }
}
