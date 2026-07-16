package roomescape.time;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class TimeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Time> findAll() {
        return entityManager.createQuery("SELECT t FROM Time t WHERE t.deleted = false", Time.class)
                .getResultList();
    }

    @Transactional
    public Time save(Time time) {
        entityManager.persist(time);
        return time;
    }

    @Transactional
    public void deleteById(Long id) {
        Time time = entityManager.find(Time.class, id);
        if (time != null) {
            entityManager.remove(time);
        }
    }
}
