package ttakkeun.ttakkeun_server.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.ExceptionHandler;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;
import ttakkeun.ttakkeun_server.converter.PetConverter;
import ttakkeun.ttakkeun_server.dto.pet.PetRequestDTO;
import ttakkeun.ttakkeun_server.dto.pet.PetResponseDTO;
import ttakkeun.ttakkeun_server.entity.*;
import ttakkeun.ttakkeun_server.entity.Record;
import ttakkeun.ttakkeun_server.entity.enums.Neutralization;
import ttakkeun.ttakkeun_server.entity.enums.PetType;
import ttakkeun.ttakkeun_server.repository.*;

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
    private final ResultRepository resultRepository;
    private final RecordRepository recordRepository;
    private final ResultProductRepository resultProductRepository;

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

    public PetResponseDTO.LoadResultDTO load(Long petId, Member member) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.PET_ID_NOT_AVAILABLE));

        if (!pet.getMember().getMemberId().equals(member.getMemberId())) {
            throw new ExceptionHandler(ErrorStatus.PET_NOT_FOUND);
        }

        return PetResponseDTO.LoadResultDTO.builder()
                .petName(pet.getPetName())
                .petImageUrl(pet.getPetImageUrl())
                .petType(pet.getPetType().name())
                .petVariety(pet.getPetVariety())
                .birth(pet.getBirth())
                .neutralization(pet.getNeutralization() == Neutralization.NEUTRALIZATION)
                .build();
    }

    public Pet add(PetRequestDTO.AddDTO request, Member member) {
        Pet newPet = PetConverter.toPet(request, member);
        return petRepository.save(newPet);
    }

    @Transactional
    public void deletePet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ExceptionHandler(PET_ID_NOT_AVAILABLE));

        // 연관된 Record의 result 엔티티들 수동 삭제
        for (Record record : pet.getRecordList()) {
            // 각 record에 대한 result 목록을 가져와서 각각 삭제
            List<Result> results = resultRepository.findByRecord(record);
            for (Result result : results) {
                // ResultProduct 삭제 (명시적으로 삭제)
                for (ResultProduct resultProduct : result.getProductList()) {
                    resultProductRepository.delete(resultProduct);
                }
                // Result 삭제
                resultRepository.delete(result);
            }
        }

        // record가 null이고 해당 pet_id를 가진 result 엔티티들 수동 삭제
        List<Result> resultsWithNullRecord = resultRepository.findByRecordIsNullAndPet(pet);
        for (Result result : resultsWithNullRecord) {
            // ResultProduct 삭제 (명시적으로 삭제)
            for (ResultProduct resultProduct : result.getProductList()) {
                resultProductRepository.delete(resultProduct);
            }
            // Result 삭제
            resultRepository.delete(result);
        }

        // 연관된 Record 삭제
        recordRepository.deleteAll(pet.getRecordList());

        // Pet 엔티티와 연결된 연관 엔티티들도 CascadeType.REMOVE를 통해 삭제됩니다.
        petRepository.delete(pet);
    }
}
