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
import ttakkeun.ttakkeun_server.dto.pet.PetRequestDTO;
import ttakkeun.ttakkeun_server.dto.pet.PetResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.enums.Neutralization;
import ttakkeun.ttakkeun_server.entity.enums.PetType;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.repository.PetRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.*;
import static ttakkeun.ttakkeun_server.entity.enums.Neutralization.NEUTRALIZATION;

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
            e.printStackTrace();
            throw new ExceptionHandler(IMAGE_NOT_SAVE);
        }

        if(newUrl == null)
            throw new ExceptionHandler(IMAGE_NOT_SAVE);

        pet.updateImage(newUrl);
        petRepository.save(pet);

        return new PetResponseDTO.PetImageDTO(pet.getPetImageUrl());
    }

    public Optional<Pet> findById(Long petId) {
        return petRepository.findById(petId);
    }

    public PetResponseDTO.EditResultDTO updateProfile(Pet pet, PetRequestDTO.AddDTO request) {
        if (request.getName() != null && !request.getName().equals("string")) {
            pet.setPetName(request.getName());
        }
        if (request.getVariety() != null && !request.getVariety().equals("string")) {
            pet.setPetVariety(request.getVariety());
        }
        if (request.getBirth() != null && !request.getBirth().equals("string")) {
            pet.setBirth(request.getBirth());
        }
        if (request.getNeutralization() != null) {
            pet.setNeutralization(request.getNeutralization() ?
                    NEUTRALIZATION : Neutralization.UNNEUTRALIZATION);
        }
        if (request.getType() != null && !request.getType().equals("string")) {
            pet.setPetType(switch (request.getType()) {
                case "CAT" -> PetType.CAT;
                case "DOG" -> PetType.DOG;
                default -> null;
            });
        }

        petRepository.save(pet);

        return PetResponseDTO.EditResultDTO.builder()
                .petName(pet.getPetName())
                .petVariety(pet.getPetVariety())
                .birth(pet.getBirth())
                .neutralization(pet.getNeutralization().equals(NEUTRALIZATION))
                .petType(pet.getPetType().equals(PetType.DOG) ? "DOG" : "CAT")
                .build();
    }
}
