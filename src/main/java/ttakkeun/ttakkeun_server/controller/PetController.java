package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.ExceptionHandler;
import ttakkeun.ttakkeun_server.converter.PetConverter;
import ttakkeun.ttakkeun_server.dto.pet.PetRequestDTO;
import ttakkeun.ttakkeun_server.dto.pet.PetResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.service.PetService;

import java.util.List;
import java.util.stream.Collectors;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pet-profile")
public class PetController {

    private final PetService petService;

    @Operation(summary = "반려동물 프로필 추가 API")
    @PostMapping("/add")
    public ApiResponse<PetResponseDTO.AddResultDTO> add(
            @AuthenticationPrincipal Member member,
            @RequestBody @Valid PetRequestDTO.AddDTO request
    ) {
        Pet newPet = petService.add(request, member);
        PetResponseDTO.AddResultDTO result = PetConverter.toAddResultDTO(newPet);

        return ApiResponse.onSuccess(result);
    }



    @Operation(summary = "특정 반려동물 프로필 조회 API")
    @GetMapping("/{pet_id}")
    public ApiResponse<PetResponseDTO.LoadResultDTO> load(
            @AuthenticationPrincipal Member member,
            @PathVariable("pet_id") Long petId
    ) {
        PetResponseDTO.LoadResultDTO resultDTO = petService.load(petId, member);
        return ApiResponse.onSuccess(resultDTO);
    }


    @Operation(summary = "로그인한 사용자의 모든 반려동물 조회 API")
    @GetMapping("/select")
    public ApiResponse<PetResponseDTO.SelectResultDTO> select(
            @AuthenticationPrincipal Member member
    ) {
        List<Pet> pets = petService.getPetsByMemberId(member.getMemberId());

        if(pets.isEmpty())
            throw new ExceptionHandler(MEMBER_NOT_HAVE_PET);


        List<PetResponseDTO.SelectDTO> petDTOs = pets.stream()
                .map(PetConverter::toSelectDTO)
                .collect(Collectors.toList());

        PetResponseDTO.SelectResultDTO resultDTO = PetResponseDTO.SelectResultDTO.builder()
                .result(petDTOs)
                .build();

        return ApiResponse.onSuccess(resultDTO);
    }

    @Operation(summary = "반려동물 프로필 이미지 수정")
    @PatchMapping(value = "/{pet_id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<PetResponseDTO.PetImageDTO> editPetImage (
            @AuthenticationPrincipal Member member,
            @PathVariable("pet_id") Long petId,
            @RequestPart("multipartFile") MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty())
            throw new ExceptionHandler(IMAGE_EMPTY);

        // pet_id로 반려동물을 조회
        Pet pet = petService.findPetByIdAndMember(petId, member);

        // 반려동물의 프로필 이미지 업데이트
        PetResponseDTO.PetImageDTO result = petService.updateProfileImage(pet, multipartFile);
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "반려동물 프로필 수정")
    @PatchMapping(value = "/edit/{pet_id}")
    public ApiResponse<PetResponseDTO.EditResultDTO> editPetprofile(
            @AuthenticationPrincipal Member member,
            @PathVariable("pet_id") Long petId,
            @RequestBody @Valid PetRequestDTO.AddDTO request
    ) {
        Pet pet = petService.findById(petId)
                .orElseThrow(() -> new ExceptionHandler(PET_ID_NOT_AVAILABLE));

        if(!pet.getMember().getMemberId().equals(member.getMemberId()))
            throw new ExceptionHandler(PET_NOT_FOUND);

        PetResponseDTO.EditResultDTO resultDTO = petService.updateProfile(pet, request);

        return ApiResponse.onSuccess(resultDTO);
    }

    @Operation(summary = "반려동물 프로필 삭제")
    @DeleteMapping(value = "/{pet_id}")
    public ApiResponse<Void> deletePet(
            @PathVariable("pet_id") Long petId
    ) {
        petService.deletePet(petId);
        return ApiResponse.onSuccess();
    }
}
