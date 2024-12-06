package ttakkeun.ttakkeun_server.apiPayLoad.exception;

import ttakkeun.ttakkeun_server.apiPayLoad.code.BaseErrorCode;

public class OAuthHandler extends GeneralException{
    public OAuthHandler(BaseErrorCode code) {
        super(code);
    }
}
