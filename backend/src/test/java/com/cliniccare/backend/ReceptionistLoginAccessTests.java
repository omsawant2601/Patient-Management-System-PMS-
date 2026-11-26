package com.cliniccare.backend;

import com.cliniccare.backend.user.AppUser;
import com.cliniccare.backend.user.AppUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReceptionistLoginAccessTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private AppUserRepository users;

    @Test
    void receptionistCanCompleteLoginButCannotListUsers() throws Exception {
        String username="receptionist-"+UUID.randomUUID();
        String password="Receptionist-test-password-123";
        AppUser receptionist=new AppUser();
        receptionist.setFullName("Test Receptionist");
        receptionist.setUsername(username);
        receptionist.setMobile("test-"+UUID.randomUUID());
        receptionist.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        receptionist.setRole(AppUser.Role.RECEPTIONIST);
        receptionist.setActive(true);
        users.saveAndFlush(receptionist);

        String tokenResponse=mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\""+username+"\",\"password\":\""+password+"\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String accessToken=new ObjectMapper().readTree(tokenResponse).path("data").path("accessToken").asText();

        mockMvc.perform(get("/api/v1/users/me").header("Authorization","Bearer "+accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("RECEPTIONIST"));
        mockMvc.perform(get("/api/v1/users").header("Authorization","Bearer "+accessToken))
                .andExpect(status().isForbidden());
    }
}
