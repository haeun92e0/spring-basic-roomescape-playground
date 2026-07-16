package roomescape.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyReservationResponse {

    private Long reservationId;
    private String theme;
    private String date;
    private String time;
    private String status;
}
