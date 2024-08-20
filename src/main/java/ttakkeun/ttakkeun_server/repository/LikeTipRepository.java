//package ttakkeun.ttakkeun_server.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import ttakkeun.ttakkeun_server.entity.Member;
//import ttakkeun.ttakkeun_server.entity.Tip;
//import ttakkeun.ttakkeun_server.entity.LikeTip;
//
//import java.util.Optional;
//
//@Repository
//public interface LikeTipRepository extends JpaRepository<LikeTip, Long> {
//
//    @Query("SELECT lt FROM LikeTip lt WHERE lt.tip.tipId = :tipId AND lt.member.memberId = :memberId")
//    Optional<LikeTip> findByTipIdAndMemberId(@Param("tipId") Long tipId, @Param("memberId") Long memberId);
//
//    int countByTip(Tip tip);
//
//    boolean existsByTipAndMember(Tip tip, Member member);
//
//    boolean existsIsLike(Tip tip, Member member);
//}
