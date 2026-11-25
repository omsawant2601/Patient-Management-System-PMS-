package com.cliniccare.backend.auth;

import com.cliniccare.backend.user.AppUser;

public interface PasswordResetDelivery {
    void send(AppUser user, String resetUrl);
}
