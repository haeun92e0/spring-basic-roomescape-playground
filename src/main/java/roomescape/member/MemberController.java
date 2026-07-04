package roomescape.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class MemberController {
    private MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members") //회원가입
    public ResponseEntity createMember(@RequestBody MemberRequest memberRequest) {
        MemberResponse member = memberService.createMember(memberRequest);
        return ResponseEntity.created(URI.create("/members/" + member.getId())).body(member);
    }

    @PostMapping("/logout") //로그아웃 시
    public ResponseEntity logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", ""); //새로운 쿠키 만들기
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); //유효시간 0초로 바꾸기 > 즉시 만료
        response.addCookie(cookie);// 기존 token을 새로운 쿠키로 덮어씀
        return ResponseEntity.ok().build();
    }
}
