package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Point {

    @Id
    @Column(name = "member_id")
    private Long memberId; // Member 엔티티의 PK를 참조

    private Integer points;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // 'memberId'를 'Member' 엔티티의 PK와 매핑
    @JoinColumn(name = "member_id")
    private Member member;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
