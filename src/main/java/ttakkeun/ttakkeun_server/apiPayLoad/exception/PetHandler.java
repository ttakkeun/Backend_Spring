package ttakkeun.ttakkeun_server.apiPayLoad.exception;

import ttakkeun.ttakkeun_server.apiPayLoad.code.BaseErrorCode;

public class PetHandler extends GeneralException {
    public PetHandler(BaseErrorCode code) {
        super(code);
    }
}
