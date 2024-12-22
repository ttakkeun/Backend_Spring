package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;
import ttakkeun.ttakkeun_server.entity.enums.InquiryType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry extends BaseEntity {

    @Id
    @Column(name = "inquiry_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    private String contents;

    private String email;

    @Enumerated(EnumType.STRING)
    private InquiryType inquiryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InquiryImage> images = new ArrayList<>();

    public List<InquiryImage> getImages() {
        return images != null ? images : new ArrayList<>();
    }
}
