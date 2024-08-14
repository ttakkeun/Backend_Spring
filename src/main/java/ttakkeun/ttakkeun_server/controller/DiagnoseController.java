package ttakkeun.ttakkeun_server.controller;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ttakkeun.ttakkeun_server.apiPayLoad.ApiResponse;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.SuccessStatus;
import ttakkeun.ttakkeun_server.dto.diagnose.*;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.Category;
//import ttakkeun.ttakkeun_server.service.DiagnoseNaverProductService;
import ttakkeun.ttakkeun_server.service.DiagnoseService;
//import ttakkeun.ttakkeun_server.dto.UpdateProductsDTO;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diagnose")
public class DiagnoseController {
    @Autowired
    private final DiagnoseService diagnoseService;

//    @Autowired
//    private final DiagnoseNaverProductService diagnoseNaverProductService;

    // 진단 버튼 클릭시 사용자의 포인트를 조회하는 API
    @Operation(summary = "사용자 포인트 조회 API")
    @GetMapping("/point")
    public ResponseEntity<ApiResponse<GetMyPointResponseDTO>> getPointsByMember(@AuthenticationPrincipal Member member) {
        try {
            if (member == null) { // 사용자 정보를 가져오지 못할 경우 UsernameNotFoundException 에러 발생
                throw new UsernameNotFoundException("인증이 필요합니다. 로그인 정보를 확인해주세요.");
            }
            Long memberId = member.getMemberId(); // 인증된 사용자의 memberId를 가져옴
            Integer point = diagnoseService.getPointsByMember(memberId);
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.of(SuccessStatus._OK, new GetMyPointResponseDTO(point));
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.MEMBER_HAS_NO_POINT, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (UsernameNotFoundException e) {
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._UNAUTHORIZED, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponse<GetMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 진단 결과 목록을 조회하는 API
    // 페이징 처리, 한 페이지당 10개 고정
    // size가 고정이므로 Path Variable로 처리하였음
    @Operation(summary = "진단 결과 목록 조회 API")
    @GetMapping("/{pet_id}/{category}/{page}")
    public ResponseEntity<ApiResponse<GetMyDiagnoseListResponseDTO>> getDiagnoseListByPet(@AuthenticationPrincipal Member member, @PathVariable(name = "pet_id") Long petId,
                                                                                              @PathVariable(name = "category") Category category,
                                                                                              @PathVariable(name = "page") int page) {
        try {
            if (member == null) { // 사용자 정보를 가져오지 못할 경우 UsernameNotFoundException 에러 발생
                throw new UsernameNotFoundException("인증이 필요합니다. 로그인 정보를 확인해주세요.");
            }
            Long memberId = member.getMemberId(); // 인증된 사용자의 memberId를 가져옴
            GetMyDiagnoseListResponseDTO getMyDiagnoseListResponseDTO = diagnoseService.getDiagnoseListByPet(memberId, petId, category, page);
            ApiResponse<GetMyDiagnoseListResponseDTO> response = ApiResponse.of(SuccessStatus._OK, getMyDiagnoseListResponseDTO);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            ApiResponse<GetMyDiagnoseListResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.PET_ID_NOT_AVAILABLE, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UsernameNotFoundException e) {
            ApiResponse<GetMyDiagnoseListResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._UNAUTHORIZED, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponse<GetMyDiagnoseListResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    // 진단시 사용자 포인트 차감 API
    @Operation(summary = "사용자 포인트 차감 API")
    @PatchMapping("/loading")
    public ResponseEntity<ApiResponse<UpdateMyPointResponseDTO>> updatePointsByMember(@AuthenticationPrincipal Member member) {
        try {
            if (member == null) { // 사용자 정보를 가져오지 못할 경우 UsernameNotFoundException 에러 발생
                throw new UsernameNotFoundException("인증이 필요합니다. 로그인 정보를 확인해주세요.");
            }
            Long memberId = member.getMemberId(); // 인증된 사용자의 memberId를 가져옴
            Integer point = diagnoseService.updatePointsByMember(memberId);
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.of(SuccessStatus._OK, new UpdateMyPointResponseDTO(point));
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.MEMBER_HAS_NO_POINT, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (UsernameNotFoundException e) {
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._UNAUTHORIZED, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponse<UpdateMyPointResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 네이버 쇼핑 API로 진단 결과 DB 정보 업데이트 API
//    @Operation(summary = "추천 제품 네이버 쇼핑 정보로 업데이트")
//    @PatchMapping("/{diagnose_id}")
//    // 등록되지 않은 제품일 경우 새로운 정보를 등록하지만, 네이버 검색 API로 등록된 제품들은 기존 제품 정보에서 태그값만 업데이트하게 되므로 PATCH 사용하였음
//    public ResponseEntity<ApiResponse<UpdateProductsResponseDTO>> updateDiagnoseProducts(@PathVariable("diagnose_id") Long resultId, @RequestBody UpdateProductsRequestDTO products) {
//        try {
//            UpdateProductsResponseDTO updatedProducts = diagnoseNaverProductService.updateDiagnoseProducts(resultId, products);
//            ApiResponse<UpdateProductsResponseDTO> response = ApiResponse.of(SuccessStatus._OK, updatedProducts);
//            return ResponseEntity.ok(response);
//        } catch (NoSuchElementException e) {
//            ApiResponse<UpdateProductsResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.DIAGNOSE_NOT_FOUND, null);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        } catch (Exception e) {
//            ApiResponse<UpdateProductsResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }


    // 진단서 상세 내용 조회 API
    @Operation(summary = "진단서 상세 내용 조회 API")
    @GetMapping("/{diagnose_id}")
    public ResponseEntity<ApiResponse<GetMyDiagnoseResponseDTO>> getDiagnose(@PathVariable("diagnose_id") Long resultId) {
        try {
            GetMyDiagnoseResponseDTO diagnose = diagnoseService.getDiagnose(resultId);
            ApiResponse<GetMyDiagnoseResponseDTO> response = ApiResponse.of(SuccessStatus._OK, diagnose);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            ApiResponse<GetMyDiagnoseResponseDTO> response = ApiResponse.ofFailure(ErrorStatus.DIAGNOSE_NOT_FOUND, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ApiResponse<GetMyDiagnoseResponseDTO> response = ApiResponse.ofFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}