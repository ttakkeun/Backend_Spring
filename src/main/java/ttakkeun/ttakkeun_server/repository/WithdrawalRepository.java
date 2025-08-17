package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ttakkeun.ttakkeun_server.entity.Withdrawal;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
}
