package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import roomescape.member.JwtTokenProvider;

@Configuration
public class JwtConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        return new JwtTokenProvider(secretKey);
    }
}
//스프링이 jwtTokenProvider 객체를 생성해 관리하게 해줌
