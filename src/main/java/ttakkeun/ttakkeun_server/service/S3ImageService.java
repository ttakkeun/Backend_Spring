package ttakkeun.ttakkeun_server.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.ExceptionHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.*;

@Slf4j
@Component
public class S3ImageService {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    public S3ImageService(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxSizeString;

    public String upload(MultipartFile image) {
        //입력받은 이미지 파일이 빈 파일인지 검증
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
            throw new ExceptionHandler(IMAGE_EMPTY);
        }
        //uploadImage를 호출하여 S3에 저장된 이미지의 public url을 반환한다.
        return this.uploadImage(image);
    }

    private String uploadImage(MultipartFile image) {
        this.validateImageFileExtention(image.getOriginalFilename());
        try {
            return this.uploadImageToS3(image);
        } catch (IOException e) {
            throw new ExceptionHandler(S3_UPLOAD_FAIL);
        }
    }

    //파일 확장자 검증(jpg, jpeg, png, gif)
    private void validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new ExceptionHandler(NO_FILE_EXTENTION);
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extention)) {
            throw new ExceptionHandler(INVALID_FILE_EXTENTION);
        }
    }

    //S3에 파일 업로드
    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        //String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); //확장자 명

        String s3FileName = UUID.randomUUID() + originalFilename; //변경된 파일 명

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is); //image를 byte[]로 변환

        ObjectMetadata metadata = new ObjectMetadata(); //metadata 생성
        //metadata.setContentType("image/" + extention);
        metadata.setContentLength(bytes.length);
        metadata.setContentType(image.getContentType());

        //S3에 요청할 때 사용할 byteInputStream 생성
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        //S3로 putObject 할 때 사용할 요청 객체
        //생성자 : bucket 이름, 파일 명, byteInputStream, metadata
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, "images/" + s3FileName, byteArrayInputStream, metadata);

        //실제로 S3에 이미지 데이터를 넣는 부분이다.
        amazonS3.putObject(putObjectRequest); // put image to S3

        byteArrayInputStream.close();
        is.close();

        return amazonS3.getUrl(bucket, "images/" + s3FileName).toString();
    }


    public void delete(String imageUrl) {
        String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1); // URL에서 파일명 추출
        amazonS3.deleteObject(bucketName, key); // S3에서 객체 삭제
    }
}