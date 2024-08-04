package ttakkeun.ttakkeun_server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.relational.core.sql.Like;
import ttakkeun.ttakkeun_server.entity.common.BaseEntity;
import ttakkeun.ttakkeun_server.entity.enums.LoginType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    @Column(name = "apple_sub")
    private String appleSub;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime refreshTokenExpiresAt;    //토큰 만료 일자

    @OneToMany(mappedBy = "memberId", cascade = CascadeType.ALL)
    private List<Pet> petList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<LikeProduct> likeProductList = new ArrayList<>();

    // refreshToken 재발급
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = LocalDateTime.now().plusDays(7);
    }
}
