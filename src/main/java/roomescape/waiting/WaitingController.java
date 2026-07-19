package roomescape.waiting;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.member.Member;
import roomescape.reservation.ReservationRepository;
import roomescape.reservation.ReservationRequest;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;

import java.net.URI;

@RestController
@RequestMapping("/waitings")
public class WaitingController {

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final TimeRepository timeRepository;
    private final ThemeRepository themeRepository;

    public WaitingController(WaitingRepository waitingRepository,
                             ReservationRepository reservationRepository,
                             TimeRepository timeRepository,
                             ThemeRepository themeRepository) {
        this.waitingRepository = waitingRepository;
        this.reservationRepository = reservationRepository;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
    }

    @PostMapping
    public ResponseEntity<WaitingResponse> create(@RequestBody ReservationRequest request, Member loginMember) {
        boolean hasReservation = reservationRepository.existsByDateAndTimeAndTheme(request.getDate(), request.getTime(), request.getTheme());

        if (!hasReservation) {
            return ResponseEntity.badRequest().build();
        }

        boolean isAlreadyWaiting = waitingRepository.existsByDateAndTimeAndTheme(request.getDate(), request.getTime(), request.getTheme(), loginMember.getId());
        if (isAlreadyWaiting) {
            return ResponseEntity.badRequest().build();
        }
        Time time = timeRepository.findById(request.getTime()).orElseThrow();
        Theme theme = themeRepository.findById(request.getTheme()).orElseThrow();

        Waiting waiting = new Waiting(request.getDate(), time, theme, loginMember);
        Waiting saved = waitingRepository.save(waiting);

        WaitingResponse response = new WaitingResponse(saved.getId(), saved.getDate(), theme.getName(), time.getValue());
        return ResponseEntity.created(URI.create("/waitings/" + saved.getId())).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        waitingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
