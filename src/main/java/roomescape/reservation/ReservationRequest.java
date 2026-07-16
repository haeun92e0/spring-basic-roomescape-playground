package roomescape.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    private String name;
    private String date;
    private Long theme;
    private Long time;

}
