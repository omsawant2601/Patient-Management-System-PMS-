package com.cliniccare.backend.audit;

import com.cliniccare.backend.user.AppUser;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditService {
    private final AuditLogRepository repository;
    public AuditService(AuditLogRepository repository){this.repository=repository;}
    public void log(AppUser user,String action,String entityType,UUID entityId,String details){
        AuditLog log=new AuditLog();
        log.setUser(user);log.setAction(action);log.setEntityType(entityType);log.setEntityId(entityId);log.setDetails(details);
        repository.save(log);
    }
}
