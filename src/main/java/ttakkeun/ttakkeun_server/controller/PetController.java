package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.ExceptionHandler;
import ttakkeun.ttakkeun_server.converter.PetConverter;
import ttakkeun.ttakkeun_server.dto.pet.PetRequestDTO;
import ttakkeun.ttakkeun_server.dto.pet.PetResponseDTO;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.service.PetService.PetCommandService;
import ttakkeun.ttakkeun_server.service.PetService.PetQueryService;
import ttakkeun.ttakkeun_server.service.PetService.PetService;

import java.util.List;
import java.util.stream.Collectors;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.MEMBER_NOT_HAVE_PET;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pet-profile")
public class PetController {

    private final PetCommandService petCommandService;
    private final PetQueryService petQueryService;
    private final PetService petService;

    @Operation(summary = "반려동물 프로필 추가 API")
    @PostMapping("/add")
    public ApiResponse<PetResponseDTO.AddResultDTO> add(
//            @RequestHeader("Authorization") String accessToken,
            @RequestBody @Valid PetRequestDTO.AddDTO request
    ) {
        Pet newPet = petCommandService.add(request);
        PetResponseDTO.AddResultDTO resultDTO = PetConverter.toAddResultDTO(newPet);

        return ApiResponse.onSuccess(resultDTO);
    }



    @Operation(summary = "특정 반려동물 프로필 조회 API")
    @GetMapping("/{pet_id}")
    public ApiResponse<PetResponseDTO.LoadResultDTO> load(
            @PathVariable("pet_id") Long petId
    ) {
        PetResponseDTO.LoadResultDTO resultDTO = petQueryService.load(petId);
        return ApiResponse.onSuccess(resultDTO);
    }


    @Operation(summary = "로그인한 사용자의 모든 반려동물 조회 API")
    @GetMapping("/select")
    public ApiResponse<PetResponseDTO.SelectResultDTO> select(
            @RequestParam("memberId") Long memberId
    ) {
        List<Pet> pets = petService.getPetsByMemberId(memberId);

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

    //    @Operation(summary = "반려동물 프로필 이미지 수정")
//    @PatchMapping("/{pet_id}/image")
//    public ApiResponse<PetResponseDTO.PetImageDTO> editPetImage (
//            @AuthenticationPrincipal Member member, @RequestPart MultipartFile multipartFile) {
//        if (multipartFile == null || multipartFile.isEmpty())
//            throw new ExceptionHandler(IMAGE_EMPTY);
//        PetResponseDTO.PetImageDTO result = memberService.updateProfileImage(member, multipartFile);
//        return ApiResponse.onSuccess(result);
//    }
}
