package ttakkeun.ttakkeun_server.service.PetService;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.apiPayLoad.ExceptionHandler;
import ttakkeun.ttakkeun_server.dto.pet.PetResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.repository.PetRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
public class PetService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final PetRepository petRepository;
    private final MemberRepository memberRepository;
    private final AmazonS3 amazonS3Client;

    public List<Pet> getPetsByMemberId(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ExceptionHandler(MEMBER_NOT_FOUND));

        return petRepository.findByMemberId(memberId);
    }

    //memberId에 해당 petId가 있는지 조회
    public Pet findPetByIdAndMember(Long petId, Member member) {
        // petId와 member를 기준으로 반려동물을 조회
        return petRepository.findByPetIdAndMember(petId, member)
                .orElseThrow(() -> new ExceptionHandler(PET_NOT_FOUND));
    }

    public PetResponseDTO.PetImageDTO updateProfileImage(Pet pet, MultipartFile multipartFile) {
        String newUrl = null;
        String fileUrl = pet.getPetImageUrl();

        if(fileUrl != null) {
            String[] url = fileUrl.split("/");
            String fileName = url[url.length - 1];
            amazonS3Client.deleteObject(bucket, "profile/" + fileName);
        }

        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String newFileName = UUID.randomUUID() + "_" + originalFilename;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            //S3에 이미지 업로드
            amazonS3Client.putObject(bucket, "profile/" + newFileName, multipartFile.getInputStream(), metadata);
            newUrl = amazonS3Client.getUrl(bucket, "profile/" + newFileName).toString();
        } catch (IOException | AmazonServiceException e) {
            throw new ExceptionHandler(IMAGE_NOT_SAVE);
        }

        if(newUrl == null)
            throw new ExceptionHandler(IMAGE_NOT_SAVE);

        pet.updateImage(newUrl);
        petRepository.save(pet);

        return new PetResponseDTO.PetImageDTO(pet.getPetImageUrl());
    }
}
