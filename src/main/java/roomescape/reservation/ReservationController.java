package roomescape.reservation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.Member;
import roomescape.theme.Theme;
import roomescape.time.Time;
import roomescape.time.TimeRepository;
import roomescape.waiting.WaitingRepository;

import roomescape.waiting.Waiting;
import roomescape.waiting.WaitingRepository;
import roomescape.waiting.WaitingWithRank;

import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final roomescape.member.MemberRepository memberRepository;
    private final roomescape.theme.ThemeRepository themeRepository;
    private final TimeRepository timeRepository;
    private final WaitingRepository waitingRepository;

    public ReservationController(ReservationRepository reservationRepository,
                                 roomescape.member.MemberRepository memberRepository,
                                 roomescape.theme.ThemeRepository themeRepository, TimeRepository timeRepository, WaitingRepository waitingRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.timeRepository = timeRepository;
        this.waitingRepository = waitingRepository;
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
        Member member = memberRepository.findById(loginMember.getId()).orElseThrow();

        String finalName = (reservationRequest.getName() != null && !reservationRequest.getName().isBlank())
                ? reservationRequest.getName()
                : loginMember.getName();

        Theme theme = themeRepository.findById(reservationRequest.getTheme()).orElseThrow();
        Time time = timeRepository.findById(reservationRequest.getTime()).orElseThrow();
        Reservation reservation = new Reservation(finalName, reservationRequest.getDate(), time, theme, member);
        Reservation saved = reservationRepository.save(reservation);


        return ResponseEntity.created(URI.create("/reservations/" + saved.getId())).body(saved);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //예약 조회할 때 대기 목록까지 같이 반환하도록 변경
    @GetMapping("/reservations-mine")
    public List<MyReservationResponse> listMine(Member loginMember) {

        List<MyReservationResponse> result = new ArrayList<>();
        List<Reservation> reservations = reservationRepository.findByMemberId(loginMember.getId());

        for(Reservation r : reservations){
            result.add(new MyReservationResponse(
                    r.getId(),
                    r.getTheme().getName(),
                    r.getDate(),
                    r.getTime().getValue(),
                    "예약"
            ));
        }

        List<WaitingWithRank> waitingsWithRank = waitingRepository.findWaitingsWithRankByMemberId(loginMember.getId());
        for (WaitingWithRank wr : waitingsWithRank) {
            Waiting w = wr.getWaiting();
            long displayRank = wr.getRank() + 1; //1번째 대기로
            result.add(new MyReservationResponse(
                    w.getId(),
                    w.getTheme().getName(),
                    w.getDate(),
                    w.getTime().getValue(),
                    displayRank + "번째 예약대기"
            ));
        }

        return result;
    }

}

