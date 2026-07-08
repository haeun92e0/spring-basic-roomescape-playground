package roomescape.member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    public AdminInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider =  jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        Cookie[] cookies = request.getCookies();

        //쿠키가 없는 경우( 로그인을 아직 안한 사용자)

        if (cookies == null){
            response.setStatus(401);
            return false;
        }

        String token = "";


        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                token = cookie.getValue();
            }
        }

        //토큰 쿠키가 없는 경우
        if (token.isBlank()){
            response.setStatus(401);
            return false;
        }

        try {
            Claims claims = jwtTokenProvider.parseToken(token);

            String role = claims.get("role", String.class);

            if (!role.equals("ADMIN")) {
                response.setStatus(403);
                return false;
            }

            return true;
        }catch (Exception e){
            response.setStatus(401);
            return false;
        }
    }

}
