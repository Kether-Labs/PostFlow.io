package io.ketherlabs.postflow.identity.infrastructure.persistence;


import io.ketherlabs.postflow.core.EntityBase;
import io.ketherlabs.postflow.identity.domain.entity.PasswordResetToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Entity
@Table(name = "password_refresh_token")
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetTokenJpaEntity extends EntityBase {


    @Column(nullable = false)
    private String tokenHash;

    @Column(nullable = false)
    private UUID userid;

    @Column(nullable = false)
    private Instant expireAt;

    @Column(nullable = false)
    private boolean used;

    @CreationTimestamp
    private LocalDateTime createdAt;



    public PasswordResetToken toDomain() {
        return PasswordResetToken.reconstruct(
                this.id,
                this.tokenHash,
                this.userid,
                this.expireAt,
                this.createdAt,
                this.used
        );
    }


    public static PasswordResetTokenJpaEntity from(PasswordResetToken token) {

        PasswordResetTokenJpaEntity t = new PasswordResetTokenJpaEntity();

        t.id = token.getId();
        t.tokenHash = token.getTokenHash();
        t.userid = token.getUserId();
        t.expireAt = token.getExpiresAt();
        t.used = token.isUsed();
        t.createdAt = token.getCreatedAt();

        return  t;
    }

}
