package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ttakkeun.ttakkeun_server.entity.LikeTip;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.ScrapTip;
import ttakkeun.ttakkeun_server.entity.Tip;

import java.util.Optional;

public interface ScrapTipRepository extends JpaRepository<ScrapTip, Integer> {

    Optional<ScrapTip> findByTipAndMember(Tip tip, Member member);

    boolean existsByTipAndMember(Tip tip, Member member);

    Page<ScrapTip> findByMember(Member member, Pageable pageable);
}