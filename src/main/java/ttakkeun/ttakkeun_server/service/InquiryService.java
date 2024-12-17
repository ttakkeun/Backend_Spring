package ttakkeun.ttakkeun_server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.converter.InquiryConverter;
import ttakkeun.ttakkeun_server.dto.inquiry.InquiryRequestDTO;
import ttakkeun.ttakkeun_server.dto.inquiry.InquiryResponseDTO;
import ttakkeun.ttakkeun_server.entity.Inquiry;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.enums.InquiryType;
import ttakkeun.ttakkeun_server.repository.InquiryRepository;

import java.util.List;

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
}
