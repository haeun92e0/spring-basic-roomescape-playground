package roomescape.theme;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class ThemeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Theme> findAll() {
        return entityManager.createQuery("SELECT t FROM Theme t WHERE t.deleted = false", Theme.class)
                .getResultList();
    }

    public Theme findById(Long id) {
        return entityManager.find(Theme.class, id);
    }

    @Transactional
    public Theme save(Theme theme) {
        entityManager.persist(theme);
        return theme;
    }

    @Transactional
    public void deleteById(Long id) {
        Theme theme = entityManager.find(Theme.class, id);
        if (theme != null) {
            theme.delete();
        }
    }
}
