package roomescape.reservation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.Member; // 👈 새로 추가된 Member 도메인 패키지 임포트 확인!

import java.net.URI;
import java.util.List;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations") //예약 목록 반환
    public List<ReservationResponse> list() {
        return reservationService.findAll();
    }

    @PostMapping("/reservations")
    public ResponseEntity create(
            @RequestBody ReservationRequest reservationRequest,
            Member loginMember //현재 로그인한 회원
    ) {

        String finalName = (reservationRequest.getName() != null && !reservationRequest.getName().isBlank())
                ? reservationRequest.getName()
                : loginMember.getName();

        // 예약 요청에 이름이 있으면 그 이름으로, 없으면 로그인한 회원 이름으로
        ReservationRequest finalRequest = new ReservationRequest(
                finalName,
                reservationRequest.getDate(),
                reservationRequest.getTheme(),
                reservationRequest.getTime()
        );


        if (finalRequest.getName() == null
                || finalRequest.getDate() == null
                || finalRequest.getTheme() == null
                || finalRequest.getTime() == null) {
            return ResponseEntity.badRequest().build();
        }

        ReservationResponse reservation = reservationService.save(finalRequest);

        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId())).body(reservation);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
