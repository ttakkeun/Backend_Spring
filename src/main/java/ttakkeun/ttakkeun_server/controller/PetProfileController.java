package ttakkeun.ttakkeun_server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.converter.PetProfileConverter;
import ttakkeun.ttakkeun_server.dto.PetProfileRequestDTO;
import ttakkeun.ttakkeun_server.dto.PetProfileResponseDTO;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.service.PetProfileService.PetProfileCommandService;
import ttakkeun.ttakkeun_server.service.PetProfileService.PetProfileQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pet-profile")
public class PetProfileController {

    private final PetProfileCommandService petProfileCommandService;
    private final PetProfileQueryService petProfileQueryService;

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
    public ApiResponse<PetProfileResponseDTO.LoadResultDTO> load(
            @PathVariable("pet_id") Long petId
    ) {
        PetProfileResponseDTO.LoadResultDTO resultDTO = petProfileQueryService.load(petId);
        return ApiResponse.onSuccess(resultDTO);
    }

}
