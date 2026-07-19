package roomescape.waiting;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.member.Member;
import roomescape.theme.Theme;
import roomescape.time.Time;

@Entity
@Table(name = "waiting")
@Getter
@NoArgsConstructor
public class Waiting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;

    @ManyToOne(fetch= FetchType.LAZY)
    private Time time;

    @ManyToOne(fetch= FetchType.LAZY)
    private Theme theme;

    @ManyToOne(fetch= FetchType.LAZY)
    private Member member;

    public Waiting(String date, Time time, Theme theme, Member member) {
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.member = member;
    }


}
