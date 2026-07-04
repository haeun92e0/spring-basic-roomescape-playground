package roomescape.time;

import org.springframework.stereotype.Service;
import roomescape.reservation.Reservation;
import roomescape.reservation.ReservationDao;

import java.util.List;

@Service
public class TimeService {
    private TimeDao timeDao;
    private ReservationDao reservationDao;

    public TimeService(TimeDao timeDao, ReservationDao reservationDao) {
        this.timeDao = timeDao;
        this.reservationDao = reservationDao;
    }

    public List<AvailableTime> getAvailableTime(String date, Long themeId) {
        List<Reservation> reservations = reservationDao.findByDateAndThemeId(date, themeId); //예약된 시간만 있는 리스트
        List<Time> times = timeDao.findAll(); //전체 시간 조회

        return times.stream() //전체 시간을 하나씩 꺼내서 검사
                .map(time -> new AvailableTime(
                        time.getId(),
                        time.getValue(),
                        reservations.stream()
                                .anyMatch(reservation -> reservation.getTime().getId().equals(time.getId()))
                ))
                .toList();
    }

    public List<Time> findAll() {
        return timeDao.findAll();
    }

    public Time save(Time time) {
        return timeDao.save(time);
    }

    public void deleteById(Long id) {
        timeDao.deleteById(id);
    }
}
