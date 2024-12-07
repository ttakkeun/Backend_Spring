package ttakkeun.ttakkeun_server.apiPayLoad.exception;

import ttakkeun.ttakkeun_server.apiPayLoad.code.BaseErrorCode;

public class MemberHandler extends GeneralException{
    public MemberHandler(BaseErrorCode code) {
        super(code);
    }
}
