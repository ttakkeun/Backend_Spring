package ttakkeun.ttakkeun_server.converter;

import org.springframework.stereotype.Component;
import ttakkeun.ttakkeun_server.entity.Image;
import ttakkeun.ttakkeun_server.entity.UserAnswer;

@Component
public class ImageConverter {
    public static Image toImage(String imageUrl, UserAnswer userAnswer) {
        return Image.builder()
                .imageUrl(imageUrl)
                .answer(userAnswer)
                .build();
    }
}
