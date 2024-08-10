package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;
import ttakkeun.ttakkeun_server.converter.PetProfileConverter;
import ttakkeun.ttakkeun_server.dto.pet.PetProfileRequestDTO;
import ttakkeun.ttakkeun_server.dto.pet.PetProfileResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.service.PetProfileService.PetProfileCommandService;
import ttakkeun.ttakkeun_server.service.PetProfileService.PetProfileQueryService;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.IMAGE_EMPTY;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pet-profile")
public class PetProfileController {

    private final PetProfileCommandService petProfileCommandService;
    private final PetProfileQueryService petProfileQueryService;

    @Operation(summary = "반려동물 프로필 추가 API")
    @PostMapping("/add")
    public ApiResponse<PetProfileResponseDTO.AddResultDTO> add(
//            @RequestHeader("Authorization") String accessToken,
            @RequestBody @Valid PetProfileRequestDTO.AddDTO request
    ) {
        Pet newPet = petProfileCommandService.add(request);
        PetProfileResponseDTO.AddResultDTO resultDTO = PetProfileConverter.toAddResultDTO(newPet);

        return ApiResponse.onSuccess(resultDTO);
    }


    @GetMapping("/{pet_id}")
    @Operation(summary = "특정 반려동물 프로필 조회 API")
    public ApiResponse<PetProfileResponseDTO.LoadResultDTO> load(
            @PathVariable("pet_id") Long petId
    ) {
        PetProfileResponseDTO.LoadResultDTO resultDTO = petProfileQueryService.load(petId);
        return ApiResponse.onSuccess(resultDTO);
    }

//    @Operation(summary = "반려동물 프로필 이미지 수정")
//    @PatchMapping("/{pet_id}/image")
//    public ApiResponse<PetProfileResponseDTO.PetImageDTO> editPetImage (
//            @AuthenticationPrincipal Member member, @RequestPart MultipartFile multipartFile) {
//        if (multipartFile == null || multipartFile.isEmpty())
//            throw new ExceptionHandler(IMAGE_EMPTY);
//        PetProfileResponseDTO.PetImageDTO result = memberService.updateProfileImage(member, multipartFile);
//        return ApiResponse.onSuccess(result);
//    }

}
