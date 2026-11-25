package com.cliniccare.backend.user;

import jakarta.validation.constraints.*;

public class UserDtos {
    public record CreateRequest(@NotBlank @Size(max=150) String fullName,
                                @NotBlank @Size(max=100) String username,
                                @Email @Size(max=254) String email,
                                @Pattern(regexp="^[6-9][0-9]{9}$") String mobile,
                                @NotBlank @Size(min=10,max=128) String password,
                                @NotNull AppUser.Role role) {}
    public record UpdateRequest(@NotBlank @Size(max=150) String fullName,
                                @Email @Size(max=254) String email,
                                @Pattern(regexp="^[6-9][0-9]{9}$") String mobile) {}
    public record StatusRequest(@NotNull Boolean active) {}
    public record Response(java.util.UUID id, String fullName, String username, String mobile,
                           AppUser.Role role, boolean active, String email) {}
}
