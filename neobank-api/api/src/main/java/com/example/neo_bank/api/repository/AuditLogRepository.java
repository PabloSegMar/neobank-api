package com.example.neo_bank.api.repository;

import com.example.neo_bank.api.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

}
