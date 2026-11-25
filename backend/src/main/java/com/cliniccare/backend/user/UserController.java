package com.cliniccare.backend.user;

import com.cliniccare.backend.common.ApiResponse;
import com.cliniccare.backend.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController @RequestMapping("/api/v1/users")
public class UserController {
    private final AppUserRepository users;
    private final AuthService auth;
    public UserController(AppUserRepository users, AuthService auth){this.users=users;this.auth=auth;}

    @GetMapping("/me")
    public ApiResponse<UserDtos.Response> me(Authentication authentication) {
        AppUser user=(AppUser) authentication.getDetails();
        return ApiResponse.success("Current user", toResponse(user));
    }

    @GetMapping @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<java.util.List<UserDtos.Response>> list() {
        return ApiResponse.success("Users", users.findAll().stream().map(this::toResponse).toList());
    }

    @PostMapping @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<UserDtos.Response> create(@Valid @RequestBody UserDtos.CreateRequest request) {
        if(users.existsByUsernameIgnoreCase(request.username()) || users.existsByMobile(request.mobile()))
            throw new com.cliniccare.backend.common.ConflictException("Username or mobile already exists");
        if(request.email()!=null && users.existsByEmailIgnoreCase(request.email()))
            throw new com.cliniccare.backend.common.ConflictException("Email already exists");
        AppUser user=new AppUser();
        user.setFullName(request.fullName()); user.setUsername(request.username()); user.setMobile(request.mobile());
        user.setEmail(request.email());
        user.setPasswordHash(auth.passwordEncoder().encode(request.password())); user.setRole(request.role());
        user.setActive(true);
        users.save(user);
        return ApiResponse.success("User created", toResponse(user));
    }

    @GetMapping("/{id}") @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<UserDtos.Response> get(@PathVariable UUID id) {
        AppUser user=users.findById(id).orElseThrow(() -> new com.cliniccare.backend.common.NotFoundException("User not found"));
        return ApiResponse.success("User", toResponse(user));
    }

    @PutMapping("/{id}") @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<UserDtos.Response> update(@PathVariable UUID id, @Valid @RequestBody UserDtos.UpdateRequest request) {
        AppUser user=users.findById(id).orElseThrow(() -> new com.cliniccare.backend.common.NotFoundException("User not found"));
        if(request.email()!=null && users.existsByEmailIgnoreCaseAndIdNot(request.email(),id))
            throw new com.cliniccare.backend.common.ConflictException("Email already exists");
        user.setFullName(request.fullName()); user.setMobile(request.mobile());
        if(request.email()!=null) user.setEmail(request.email());
        user.setUpdatedAt(java.time.Instant.now());
        users.save(user);
        return ApiResponse.success("User updated", toResponse(user));
    }

    @PatchMapping("/{id}/status") @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<UserDtos.Response> status(@PathVariable UUID id, @Valid @RequestBody UserDtos.StatusRequest request) {
        AppUser user=users.findById(id).orElseThrow(() -> new com.cliniccare.backend.common.NotFoundException("User not found"));
        user.setActive(request.active()); user.setUpdatedAt(java.time.Instant.now()); users.save(user);
        return ApiResponse.success("User status updated", toResponse(user));
    }

    private UserDtos.Response toResponse(AppUser u) {
        return new UserDtos.Response(u.getId(),u.getFullName(),u.getUsername(),u.getMobile(),
                u.getRole(),u.isActive(),u.getEmail());
    }
}
