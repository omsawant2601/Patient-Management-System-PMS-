package com.cliniccare.backend.audit;

import com.cliniccare.backend.common.ApiResponse;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/audit")
public class AuditController {
    private final AuditLogRepository repository;
    public AuditController(AuditLogRepository repository){this.repository=repository;}
    @GetMapping @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<Page<AuditResponse>> list(@RequestParam(defaultValue="0") int page,
                                                 @RequestParam(defaultValue="50") int size){
        Page<AuditResponse> result=repository.findAllByOrderByCreatedAtDesc(PageRequest.of(page,size))
                .map(AuditResponse::from);
        return ApiResponse.success("Audit logs",result);
    }
    public record AuditResponse(java.util.UUID id,String action,String entityType,java.util.UUID entityId,
                                String ipAddress,java.time.Instant createdAt){
        static AuditResponse from(AuditLog log){
            return new AuditResponse(log.getId(),log.getAction(),log.getEntityType(),log.getEntityId(),
                    log.getIpAddress(),log.getCreatedAt());
        }
    }
}
