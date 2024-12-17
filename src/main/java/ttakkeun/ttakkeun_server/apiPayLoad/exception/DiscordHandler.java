package ttakkeun.ttakkeun_server.apiPayLoad.exception;

import ttakkeun.ttakkeun_server.apiPayLoad.code.BaseErrorCode;

public class DiscordHandler extends GeneralException{
    public DiscordHandler(BaseErrorCode code) {
        super(code);
    }
}
