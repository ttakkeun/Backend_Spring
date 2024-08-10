package ttakkeun.ttakkeun_server.service.PetProfileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PetProfileImageService {

}
    // pet 프로필 이미지 수정
    public MemberResponseDto.profileDto updateProfileImage(Member member, MultipartFile multipartFile) {
        String newUrl = null;
        String fileUrl = member.getImageUrl();

        if(fileUrl != null) {
            String[] url = fileUrl.split("/");
            amazonS3Client.deleteObject(bucket, url[3]);
        }

        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String newfileName = UUID.randomUUID() + "_" + originalFilename;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            //S3에 저장
            amazonS3Client.putObject(bucket, "profile/" +newfileName, multipartFile.getInputStream(), metadata);
            newUrl = amazonS3Client.getUrl(bucket, "profile/" + newfileName).toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(AmazonServiceException e){
            e.printStackTrace();
        }

        if(newUrl == null)
            throw new ExceptionHandler(ErrorStatus.IMAGE_NOT_SAVE);
}
