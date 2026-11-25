package com.cliniccare.backend.config;

import com.cliniccare.backend.auth.AuthService;
import com.cliniccare.backend.user.AppUser;
import com.cliniccare.backend.user.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class UserBootstrap implements ApplicationRunner {
    private final AppUserRepository users;
    private final AuthService auth;
    @Value("${CLINICCARE_BOOTSTRAP_DOCTOR_USERNAME:}") String doctorUsername;
    @Value("${CLINICCARE_BOOTSTRAP_DOCTOR_PASSWORD:}") String doctorPassword;
    @Value("${CLINICCARE_BOOTSTRAP_DOCTOR_EMAIL:}") String doctorEmail;

    public UserBootstrap(AppUserRepository users, AuthService auth) {
        this.users=users; this.auth=auth;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!doctorUsername.isBlank() && !doctorPassword.isBlank()
                && users.findByUsernameIgnoreCase(doctorUsername).isEmpty()) {
            AppUser user=new AppUser();
            user.setUsername(doctorUsername);
            user.setFullName("Clinic Doctor");
            user.setEmail(doctorEmail.isBlank()?null:doctorEmail);
            user.setMobile("9999999999");
            user.setRole(AppUser.Role.DOCTOR);
            user.setPasswordHash(auth.passwordEncoder().encode(doctorPassword));
            user.setActive(true);
            users.save(user);
        }
    }
}
