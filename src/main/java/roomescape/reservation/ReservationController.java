package roomescape.reservation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.Member; // 👈 새로 추가된 Member 도메인 패키지 임포트 확인!
import roomescape.theme.Theme;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final roomescape.member.MemberRepository memberRepository;
    private final roomescape.theme.ThemeRepository themeRepository;

    public ReservationController(ReservationRepository reservationRepository,
                                 roomescape.member.MemberRepository memberRepository,
                                 roomescape.theme.ThemeRepository themeRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
    }

    @GetMapping("/reservations") //예약 목록 반환
    public List<Reservation> list() {
        return reservationRepository.findAll();
    }

    @PostMapping("/reservations")
    public ResponseEntity create(
            @RequestBody ReservationRequest reservationRequest,
            Member loginMember //현재 로그인한 회원
    ) {
        Member member = memberRepository.findById(loginMember.getId());

        String finalName = (reservationRequest.getName() != null && !reservationRequest.getName().isBlank())
                ? reservationRequest.getName()
                : loginMember.getName();

        Theme theme = themeRepository.findById(reservationRequest.getTheme());
        roomescape.time.Time time = new roomescape.time.Time(reservationRequest.getTime(), "10:00");
        Reservation reservation = new Reservation(finalName, reservationRequest.getDate(), time, theme, member);
        Reservation saved = reservationRepository.save(reservation);


        return ResponseEntity.created(URI.create("/reservations/" + saved.getId())).body(saved);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations-mine")
    public List<MyReservationResponse> listMine(Member loginMember) {
        return reservationRepository.findByMemberId(loginMember.getId()).stream()
                .map(r -> new MyReservationResponse(
                        r.getId(),
                        r.getTheme().getName(),
                        r.getDate(),
                        r.getTime().getValue(),
                        "예약"
                ))
                .collect(Collectors.toList());
    }
}
