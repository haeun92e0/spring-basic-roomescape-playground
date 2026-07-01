package roomescape.reservation;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.theme.Theme;
import roomescape.time.Time;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class ReservationDao {

    private final JdbcTemplate jdbcTemplate;

    public ReservationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    //의존성 주입

    public List<Reservation> findAll() {//예약 정보 전부 가져오기
        return jdbcTemplate.query(//time과 theme 테이블 조인
                "SELECT r.id AS reservation_id, r.name as reservation_name, r.date as reservation_date, " +
                        "t.id AS theme_id, t.name AS theme_name, t.description AS theme_description, " +
                        "ti.id AS time_id, ti.time_value AS time_value " +
                        "FROM reservation r " +
                        "JOIN theme t ON r.theme_id = t.id " +
                        "JOIN time ti ON r.time_id = ti.id",

                (rs, rowNum) -> new Reservation(//reservation 객체로 바꿈
                        rs.getLong("reservation_id"),
                        rs.getString("reservation_name"),
                        rs.getString("reservation_date"),
                        new Time(
                                rs.getLong("time_id"),
                                rs.getString("time_value")
                        ),
                        new Theme(
                                rs.getLong("theme_id"),
                                rs.getString("theme_name"),
                                rs.getString("theme_description")
                        )));
    }

    public Reservation save(ReservationRequest reservationRequest) {//예약 생성
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO reservation(date, name, theme_id, time_id) VALUES (?, ?, ?, ?)", new String[]{"id"});
            ps.setString(1, reservationRequest.getDate());
            ps.setString(2, reservationRequest.getName());
            ps.setLong(3, reservationRequest.getTheme());
            ps.setLong(4, reservationRequest.getTime());
            return ps;
        }, keyHolder);

        Time time = jdbcTemplate.queryForObject("SELECT * FROM time WHERE id = ?",
                (rs, rowNum) -> new Time(rs.getLong("id"), rs.getString("time_value")),
                reservationRequest.getTime());

        Theme theme = jdbcTemplate.queryForObject("SELECT * FROM theme WHERE id = ?",
                (rs, rowNum) -> new Theme(rs.getLong("id"), rs.getString("name"), rs.getString("description")),
                reservationRequest.getTheme());

        return new Reservation(
                keyHolder.getKey().longValue(),
                reservationRequest.getName(),
                reservationRequest.getDate(),
                time, //timeid가 아닌 time이 들어가기 때문에
                theme
        );
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM reservation WHERE id = ?", id);
    }
    //날짜와 테마로 예약 찾기
    public List<Reservation> findReservationsByDateAndTheme(String date, Long themeId) {
        return jdbcTemplate.query(
                "SELECT r.id AS reservation_id, r.name as reservation_name, r.date as reservation_date, " +
                        "t.id AS theme_id, t.name AS theme_name, t.description AS theme_description, " +
                        "ti.id AS time_id, ti.time_value AS time_value " +
                        "FROM reservation r " +
                        "JOIN theme t ON r.theme_id = t.id " +
                        "JOIN time ti ON r.time_id = ti.id" +
                        "WHERE r.date = ? AND r.theme_id = ?",
                new Object[]{date, themeId},
                (rs, rowNum) -> new Reservation(
                        rs.getLong("reservation_id"),
                        rs.getString("reservation_name"),
                        rs.getString("reservation_date"),
                        new Time(
                                rs.getLong("time_id"),
                                rs.getString("time_value")
                        ),
                        new Theme(
                                rs.getLong("theme_id"),
                                rs.getString("theme_name"),
                                rs.getString("theme_description")
                        )));
    }

    public List<Reservation> findByDateAndThemeId(String date, Long themeId) {
        return jdbcTemplate.query(
                "SELECT r.id AS reservation_id, r.name as reservation_name, r.date as reservation_date, " +
                        "t.id AS theme_id, t.name AS theme_name, t.description AS theme_description, " +
                        "ti.id AS time_id, ti.time_value AS time_value " +
                        "FROM reservation r " +
                        "JOIN theme t ON r.theme_id = t.id " +
                        "JOIN time ti ON r.time_id = ti.id " +
                        "WHERE r.date = ? AND r.theme_id = ?",
                new Object[]{date, themeId},
                (rs, rowNum) -> new Reservation(
                        rs.getLong("reservation_id"),
                        rs.getString("reservation_name"),
                        rs.getString("reservation_date"),
                        new Time(
                                rs.getLong("time_id"),
                                rs.getString("time_value")
                        ),
                        new Theme(
                                rs.getLong("theme_id"),
                                rs.getString("theme_name"),
                                rs.getString("theme_description")
                        )));
    }
}
