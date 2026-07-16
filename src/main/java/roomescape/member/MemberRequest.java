package roomescape.member;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest { //회원가입 요청
    private String name;
    private String email;
    private String password;

}
