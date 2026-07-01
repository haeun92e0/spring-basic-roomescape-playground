package roomescape.member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

//컨트롤러의 매개변수를 자동으로 만들어주는 클래스
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    private final String secretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.getParameterType().equals(Member.class);
    }//컨트롤러의 매개변수가 member 타입이면 처리할 수 있다는 뜻

    @Override //Member객체를 실제로 만드는 함수
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(); //현재 HTTP 요청 가져오기
        Cookie[] cookies = request.getCookies(); //쿠키 가져오기

        if (cookies == null) {
            throw new IllegalArgumentException("토큰 쿠키가 존재하지 않습니다.");
        }

        String token = "";
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {//이름이 token인 쿠키 찾기
                token = cookie.getValue(); //JWT 문자열 저장
            }
        }

        // 토큰 파싱하여 회원 정보 추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)//JWT 해석
                .getBody();
        //정보 꺼내기
        Long id = Long.parseLong(claims.getSubject());
        String name = claims.get("name", String.class);
        String role = claims.get("role", String.class);

        return new Member(id, name, "", role); // 파싱된 정보로 임시 Member 객체 생성하여 반환
    }
}
//브라우저가 보낸 JWT 쿠키를 읽어서 그 안에 저장된 회원 정보를 Member 객체로 복원한 뒤, 컨트롤러의 Member loginMember 매개변수에 자동으로
//넣어주는 역할
