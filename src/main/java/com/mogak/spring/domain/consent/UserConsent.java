package com.mogak.spring.domain.consent;

import com.mogak.spring.global.BaseEntity;
import com.mogak.spring.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Table(
        name = "user_consent",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_consent_user_item",
                        columnNames = {"user_id", "consent_item_id"}
                )
        }
)
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserConsent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_consent_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_item_id", nullable = false)
    private ConsentItem consentItem;

    @Column(nullable = false)
    private boolean agreed;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    public void update(boolean agreed, LocalDateTime changedAt) {
        this.agreed = agreed;
        if (agreed) {
            this.agreedAt = changedAt;
            this.withdrawnAt = null;
            return;
        }
        this.agreedAt = null;
        this.withdrawnAt = changedAt;
    }
}
