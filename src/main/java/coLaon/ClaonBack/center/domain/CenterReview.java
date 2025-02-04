package coLaon.ClaonBack.center.domain;

import coLaon.ClaonBack.common.domain.BaseEntity;
import coLaon.ClaonBack.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "tb_center_review")
@NoArgsConstructor
public class CenterReview extends BaseEntity {
    @Column(name = "rank", nullable = false)
    private Integer rank;
    @Column(name = "content")
    private String content;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;
    @ManyToOne(targetEntity = Center.class)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;

    private CenterReview(
            Integer rank,
            String content,
            User writer,
            Center center
    ) {
        this.rank = rank;
        this.content = content;
        this.writer = writer;
        this.center = center;
    }

    private CenterReview(
            String id,
            Integer rank,
            String content,
            User writer,
            Center center,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        super(id, createdAt, updatedAt);
        this.rank = rank;
        this.content = content;
        this.writer = writer;
        this.center = center;
    }

    public static CenterReview of(
            String id,
            Integer rank,
            String content,
            User writer,
            Center center,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new CenterReview(
                id,
                rank,
                content,
                writer,
                center,
                createdAt,
                updatedAt
        );
    }

    public static CenterReview of(
            Integer rank,
            String content,
            User writer,
            Center center
    ) {
        return new CenterReview(
                rank,
                content,
                writer,
                center
        );
    }

    public void update(Integer rank, String content) {
        this.rank = rank;
        this.content = content;
    }
}
