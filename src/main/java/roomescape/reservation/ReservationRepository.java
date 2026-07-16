package roomescape.reservation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class ReservationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Reservation> findAll() {
        return entityManager.createQuery("SELECT r FROM Reservation r JOIN FETCH r.time JOIN FETCH r.theme", Reservation.class)
                .getResultList();
    }

    public List<Reservation> findByMemberId(Long memberId) {
        return entityManager.createQuery("SELECT r FROM Reservation r JOIN FETCH r.time JOIN FETCH r.theme WHERE r.member.id = :memberId", Reservation.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Transactional
    public Reservation save(Reservation reservation) {
        entityManager.persist(reservation);
        return reservation;
    }

    @Transactional
    public void deleteById(Long id) {
        Reservation reservation = entityManager.find(Reservation.class, id);
        if (reservation != null) {
            entityManager.remove(reservation);
        }
    }

    public List<Reservation> findByDateAndThemeId(String date, Long themeId) {
        return entityManager.createQuery("SELECT r FROM Reservation r WHERE r.date = :date AND r.theme.id = :themeId", Reservation.class)
                .setParameter("date", date)
                .setParameter("themeId", themeId)
                .getResultList();
    }
}
