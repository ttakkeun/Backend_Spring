package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.converter.PetConverter;
import ttakkeun.ttakkeun_server.dto.pet.PetRequestDTO;
import ttakkeun.ttakkeun_server.dto.pet.PetResponseDTO;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.service.PetService.PetCommandService;
import ttakkeun.ttakkeun_server.service.PetService.PetQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pet-profile")
public class PetController {

    private final PetCommandService petCommandService;
    private final PetQueryService petQueryService;

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

}
