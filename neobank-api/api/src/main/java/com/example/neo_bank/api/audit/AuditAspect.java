package com.example.neo_bank.api.audit;

import com.example.neo_bank.api.model.AuditLog;
import com.example.neo_bank.api.repository.AuditLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    public AuditAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @AfterReturning("@annotation(audit)")
    public void logAudit(JoinPoint joinPoint, Audit audit) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null) ? auth.getName() : "ANONYMOUS";

            String action = audit.action();
            String details = "Método ejecutado: " + joinPoint.getSignature().getName();

            AuditLog log = new AuditLog();
            log.setUsername(username);
            log.setAction(action);
            log.setDetails(details);
            log.setTimestamp(LocalDateTime.now());

            auditLogRepository.save(log);

            System.out.println("Auditoria guardada: " + action + " por " + username);

        } catch (Exception ex) {
            System.err.println("Error guardando la auditoria: " + ex.getMessage());
        }
    }

}
