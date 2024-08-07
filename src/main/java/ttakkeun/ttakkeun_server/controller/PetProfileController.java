package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.converter.PetProfileConverter;
import ttakkeun.ttakkeun_server.dto.pet.PetProfileRequestDTO;
import ttakkeun.ttakkeun_server.dto.pet.PetProfileResponseDTO;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.service.PetProfileService.PetProfileCommandService;
import ttakkeun.ttakkeun_server.service.PetProfileService.PetProfileQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pet-profile")
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

}
