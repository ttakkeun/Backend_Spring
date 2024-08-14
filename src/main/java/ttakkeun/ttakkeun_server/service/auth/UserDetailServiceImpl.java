package ttakkeun.ttakkeun_server.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.apiPayLoad.ExceptionHandler;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;
import ttakkeun.ttakkeun_server.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        System.out.println("로그인한 memberId : " + memberId);
        UserDetails result = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return result;
    }
}
