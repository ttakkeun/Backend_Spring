package ttakkeun.ttakkeun_server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ttakkeun.ttakkeun_server.jwt.JwtAuthenticationFilter;
import ttakkeun.ttakkeun_server.service.JwtService;
import ttakkeun.ttakkeun_server.service.auth.UserDetailServiceImpl;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration  authenticationConfiguration;

    @Autowired
    private final JwtService jwtService;

    @Bean
    @Order(0)
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring()
                .requestMatchers("/swagger-ui/**", "/swagger/**", "/swagger-resources/**", "/swagger-ui.html", "/test",
                        "/configuration/ui",  "/v3/api-docs/**", "/api/auth/refresh", "/api/auth/apple/**", "/api/record/register/**", "/api/record/detail/**",
                        "/api/diagnose/result/**", "/test/diagnose/**", "/api/ChatGPT/**");
    }

    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception
    { return authConfiguration.getAuthenticationManager(); }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(Customizer.withDefaults())
                .sessionManagement((sessionManagement) ->
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers(
                                        AntPathRequestMatcher.antMatcher("/api/auth/**")
                                ).authenticated()
                                .anyRequest().authenticated()
                )
                .headers(
                        headersConfigurer ->
                                headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration), jwtService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
