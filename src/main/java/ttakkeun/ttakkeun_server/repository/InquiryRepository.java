package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Inquiry;
import ttakkeun.ttakkeun_server.entity.Member;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByMember(Member member);
}
