package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAppleSub(String sub);

    Optional<Member> findByMemberId(Long memberId);

    Optional<Member> findByRefreshToken(String refreshToken);

    Optional<Member> findByEmail(String email);
}
