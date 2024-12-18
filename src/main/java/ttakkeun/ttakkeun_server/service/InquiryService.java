package ttakkeun.ttakkeun_server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.ExceptionHandler;
import ttakkeun.ttakkeun_server.converter.InquiryConverter;
import ttakkeun.ttakkeun_server.dto.inquiry.InquiryRequestDTO;
import ttakkeun.ttakkeun_server.dto.inquiry.InquiryResponseDTO;
import ttakkeun.ttakkeun_server.entity.Inquiry;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.InquiryType;
import ttakkeun.ttakkeun_server.repository.InquiryRepository;

import java.util.List;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.PET_NOT_FOUND;
import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus._NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryConverter inquiryConverter;
    private final InquiryRepository inquiryRepository;

    public InquiryResponseDTO.AddResultDTO addInquiry(
            InquiryRequestDTO inquiryRequestDTO,
            InquiryType inquiryType,
            List<MultipartFile> multipartFile,
            Member member
    ) {
        Inquiry newInquiry = inquiryConverter.toInquiry(
                inquiryRequestDTO,
                inquiryType,
                multipartFile,
                member
                );

        inquiryRepository.save(newInquiry);
        return InquiryResponseDTO.AddResultDTO.builder()
                .inquiryId(newInquiry.getInquiryId())
                .build();
    }

    public List<InquiryResponseDTO.getResultDTO> getInquiry(Member member) {
        List<Inquiry> inquiries = inquiryRepository.findByMember(member);

        return inquiries.stream()
                .map(inquiryConverter::toDTO)
                .toList();
    }
}
