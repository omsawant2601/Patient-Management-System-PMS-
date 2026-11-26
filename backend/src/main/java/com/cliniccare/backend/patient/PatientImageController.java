package com.cliniccare.backend.patient;

import com.cliniccare.backend.common.ApiResponse;
import com.cliniccare.backend.user.AppUser;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients/{patientId}")
public class PatientImageController {
    private final PatientImageService images;

    public PatientImageController(PatientImageService images) {
        this.images=images;
    }

    @PostMapping(path="/photo",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PatientDtos.Response> replaceProfilePhoto(@PathVariable UUID patientId,
                                                                  @RequestPart("photo") MultipartFile photo) {
        return ApiResponse.success("Patient profile picture updated",images.replaceProfilePhoto(patientId,photo));
    }

    @DeleteMapping("/photo")
    public ApiResponse<Void> removeProfilePhoto(@PathVariable UUID patientId) {
        images.removeProfilePhoto(patientId);
        return ApiResponse.success("Patient profile picture removed",null);
    }

    @GetMapping("/photo")
    public ResponseEntity<Resource> profilePhoto(@PathVariable UUID patientId) {
        return imageResponse(images.profilePhoto(patientId));
    }

    @GetMapping("/gallery")
    public ApiResponse<List<PatientImageDtos.Comparison>> gallery(@PathVariable UUID patientId) {
        return ApiResponse.success("Patient before-and-after gallery",images.gallery(patientId));
    }

    @PostMapping(path="/gallery",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PatientImageDtos.Comparison> addComparison(@PathVariable UUID patientId,
            @RequestParam("before") MultipartFile before,@RequestParam("after") MultipartFile after,
            @RequestParam(required=false) @Size(max=500) String caption,Authentication authentication) {
        return ApiResponse.success("Before-and-after comparison added",
                images.addComparison(patientId,before,after,caption,(AppUser)authentication.getDetails()));
    }

    @GetMapping("/gallery/{comparisonId}/{side}")
    public ResponseEntity<Resource> comparisonImage(@PathVariable UUID patientId,
            @PathVariable UUID comparisonId,@PathVariable String side) {
        return imageResponse(images.comparisonImage(patientId,comparisonId,side));
    }

    @DeleteMapping("/gallery/{comparisonId}")
    public ApiResponse<Void> removeComparison(@PathVariable UUID patientId,@PathVariable UUID comparisonId) {
        images.removeComparison(patientId,comparisonId);
        return ApiResponse.success("Before-and-after comparison removed",null);
    }

    private ResponseEntity<Resource> imageResponse(PatientImageStorage.StoredImage image) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.mediaType()))
                .contentLength(image.size())
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(image.fileName()).build().toString())
                .header("X-Content-Type-Options","nosniff")
                .body(image.resource());
    }
}
