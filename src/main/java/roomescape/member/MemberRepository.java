package roomescape.member;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import roomescape.theme.Theme;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class MemberRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Theme> findAll() {
        return entityManager.createQuery("SELECT t FROM Theme t WHERE t.deleted = false", Theme.class)
                .getResultList();
    }

    @Transactional
    public Member save(Member member) {
        entityManager.persist(member);
        return member;
    }

    @Transactional
    public void deleteById(Long id) {
        Theme theme = entityManager.find(Theme.class, id);
        if (theme != null) {
            theme.delete();
        }
    }

    public Member findById(Long id) {
        return entityManager.find(Member.class, id);
    }

    public Member findByEmailAndPassword(String email, String password) {
        try {
            return entityManager.createQuery("SELECT m FROM Member m WHERE m.email = :email AND m.password = :password", Member.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            // 이메일이나 비밀번호가 틀려서 결과가 없을 때 안전하게 처리
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
    }
}
