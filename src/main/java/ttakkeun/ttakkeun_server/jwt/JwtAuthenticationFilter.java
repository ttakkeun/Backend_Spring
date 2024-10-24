package ttakkeun.ttakkeun_server.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;
import ttakkeun.ttakkeun_server.service.JwtService;

import java.io.IOException;
import java.rmi.server.ExportException;

// Jwt 토큰으로 인증하는 필터입니다.

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter{

    private JwtService jwtService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService) {
        super(authenticationManager);
        this.jwtService =jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 헤더에서 토큰 가져오기
        String token = jwtService.resolveToken(request);
        //String requestURI = request.getRequestURI();

        // 토큰 존재 여부 및 토큰 검증
        if (StringUtils.isNotEmpty(token)) {
            if (jwtService.validateTokenBoolean(token)) {
                logger.info("토큰 검증");

                //유효한 토큰을 통해 사용자 정보 추출
                Authentication authentication = jwtService.getAuthentication(token);

                //SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("username", authentication.getName());
                logger.info("SecurityContext에 인증 정보를 저장했습니다");
            } else {
                // 유효하지 않은 토큰 처리
                throw new ExpiredJwtException(null, null, "유효하지 않은 AccessToken 입니다.");
            }
        } else {
            logger.warn("Authorization 헤더가 없거나 비어 있습니다");
        }

        chain.doFilter(request, response);
    }
}