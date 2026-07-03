package roomescape.member;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private MemberDao memberDao;
    private final String secretKey;

    public MemberService(MemberDao memberDao, @Value("${jwt.secret}") String secretKey) {
        this.memberDao = memberDao;
        this.secretKey = secretKey;
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        //회원가입 요청을 받아서 회원을 만들고 응답데이터 반환
        Member member = memberDao.save(new Member(memberRequest.getName(), memberRequest.getEmail(), memberRequest.getPassword(), "USER"));
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public String login(LoginRequest loginRequest) {
        Member member = memberDao.findByEmailAndPassword(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        return Jwts.builder()
                .setSubject(member.getId().toString())
                .claim("name", member.getName())
                .claim("role", member.getRole())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }


}
