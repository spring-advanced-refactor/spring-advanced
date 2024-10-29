package org.example.expert.domain.log.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminAccessLogRepository extends JpaRepository<AdminAccessLog, Long> {
}
