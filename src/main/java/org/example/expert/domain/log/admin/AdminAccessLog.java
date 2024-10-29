package org.example.expert.domain.log.admin;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.Timestamped;
import org.example.expert.domain.user.User;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "admin_access_log")
public class AdminAccessLog extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String requestUrl;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    @Column(nullable = false)
    private LocalDateTime accessTime;

    @Builder
    public AdminAccessLog(User user, String requestUrl, String requestBody, String responseBody, LocalDateTime accessTime) {
        this.user = user;
        this.requestUrl = requestUrl;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.accessTime = accessTime;
    }
}
