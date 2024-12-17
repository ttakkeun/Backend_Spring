package ttakkeun.ttakkeun_server.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.dto.inquiry.InquiryRequestDTO;
import ttakkeun.ttakkeun_server.entity.Inquiry;
import ttakkeun.ttakkeun_server.entity.InquiryImage;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.TipImage;
import ttakkeun.ttakkeun_server.entity.enums.InquiryType;
import ttakkeun.ttakkeun_server.repository.InquiryImageRepository;
import ttakkeun.ttakkeun_server.service.S3ImageService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InquiryConverter {
    private final S3ImageService s3ImageService;
    private final InquiryImageRepository inquiryImageRepository;

    public Inquiry toInquiry(
            InquiryRequestDTO inquiryRequestDTO,
            InquiryType inquiryType,
            List<MultipartFile> multipartFile,
            Member member
    ) {
        Inquiry inquiry = Inquiry.builder()
                .contents(inquiryRequestDTO.getContents())
                .email(inquiryRequestDTO.getEmail())
                .inquiryType(inquiryType)
                .member(member)
                .build();

        if(multipartFile != null && !multipartFile.isEmpty()) {
            List<InquiryImage> Images = multipartFile.stream()
                    .map(image -> {
                        String imageUrl = s3ImageService.upload(image);
                        InquiryImage inquiryImage = InquiryImage.builder()
                                .imageUrl(imageUrl)
                                .inquiry(inquiry)
                                .build();
                        inquiryImageRepository.save(inquiryImage);
                        return inquiryImage;
                    })
                    .toList();

            inquiry.getImages().addAll(Images);
        }

        return inquiry;
    }
}
