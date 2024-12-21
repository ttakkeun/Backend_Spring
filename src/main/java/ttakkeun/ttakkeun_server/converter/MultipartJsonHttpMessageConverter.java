package ttakkeun.ttakkeun_server.converter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MultipartJsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

	public MultipartJsonHttpMessageConverter() {
		super(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
		throws IOException, HttpMessageNotReadableException {
		String json = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
		return new ObjectMapper().readValue(json, clazz);
	}

	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage)
		throws IOException, HttpMessageNotWritableException {
		// JSON 출력에 사용
		byte[] data = new ObjectMapper().writeValueAsBytes(object);
		outputMessage.getBody().write(data);
	}
}
