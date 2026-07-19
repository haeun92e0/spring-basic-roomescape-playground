package roomescape.reservation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r JOIN FETCH r.time JOIN FETCH r.theme JOIN FETCH r.member")
    List<Reservation> findAllWithFetchJoin();

    @Query("SELECT r FROM Reservation r JOIN FETCH r.time JOIN FETCH r.theme JOIN FETCH r.member WHERE r.member.id = :memberId")
    List<Reservation> findByMemberId(@Param("memberId") Long memberId);

    //중복 예약 방지 검증하ㅣ기
    boolean existsByDateAndTimeAndTheme(String date, Long timeId, Long themeId);
}
