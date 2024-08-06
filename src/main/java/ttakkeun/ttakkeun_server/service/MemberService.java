package ttakkeun.ttakkeun_server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.repository.MemberRepository;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Optional<Member> findMemberById(Long id) {
        return memberRepository.findById(id);
    }
}
