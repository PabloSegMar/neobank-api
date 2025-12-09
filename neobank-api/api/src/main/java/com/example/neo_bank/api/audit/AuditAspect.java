package com.example.neo_bank.api.audit;

import com.example.neo_bank.api.model.AuditLog;
import com.example.neo_bank.api.repository.AuditLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Aspecto transversal para la auditoría de operaciones críticas.
 * <p>
 * Utiliza <b>Spring AOP</b> para interceptar métodos anotados con {@link Audit}.
 * Registra automáticamente quién hizo qué y cuándo, desacoplando la lógica de
 * registro de la lógica de negocio principal.
 */
@Aspect
@Component
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

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

            logger.info("AUDITORÍA: Acción '{}' registrada por usuario '{}'", action, username);
        } catch (Exception ex) {
            logger.error("FALLO AUDITORÍA_ No se pudo guardar el log de '{}'", audit.action(), ex);
        }
    }

}
