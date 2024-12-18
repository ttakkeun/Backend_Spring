package ttakkeun.ttakkeun_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.dto.inquiry.InquiryRequestDTO;
import ttakkeun.ttakkeun_server.dto.inquiry.InquiryResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.InquiryType;
import ttakkeun.ttakkeun_server.service.InquiryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(summary = "문의하기 API")
    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ApiResponse<InquiryResponseDTO.AddResultDTO> add(
            @AuthenticationPrincipal Member member,
            @RequestPart @Valid InquiryRequestDTO inquiryRequestDTO,
            @RequestPart(required = false) List<MultipartFile> multipartFile
    ) {

        InquiryResponseDTO.AddResultDTO resultDTO =  inquiryService.addInquiry(
                inquiryRequestDTO, multipartFile, member);

        return ApiResponse.onSuccess(resultDTO);
    }

    @Operation(summary = "문의 내용 조회하기 API")
    @GetMapping(value = "/myInquiry")
    public ApiResponse<List<InquiryResponseDTO.getResultDTO>> getInquiry(
            @AuthenticationPrincipal Member member
    ) {
        List<InquiryResponseDTO.getResultDTO> resultDTO = inquiryService.getInquiry(member);

        return ApiResponse.onSuccess(resultDTO);
    }
 }
