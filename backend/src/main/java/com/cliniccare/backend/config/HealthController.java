package com.cliniccare.backend.config;

import org.springframework.web.bind.annotation.*;
import com.cliniccare.backend.common.ApiResponse;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<HealthResponse> health() {
        return ApiResponse.success("Backend connected successfully",
                new HealthResponse("ClinicCare","RUNNING"));
    }
    public record HealthResponse(String application,String status){}
}