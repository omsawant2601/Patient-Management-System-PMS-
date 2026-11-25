package com.cliniccare.backend.whatsapp;

import com.cliniccare.backend.common.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/webhooks/whatsapp")
public class WhatsAppWebhookController {
    @Value("${whatsapp.verify-token:}") private String verifyToken;
    private final WhatsAppWebhookService webhookService;
    public WhatsAppWebhookController(WhatsAppWebhookService webhookService){this.webhookService=webhookService;}

    @GetMapping
    public ResponseEntity<String> verify(@RequestParam("hub.mode") String mode,
                                         @RequestParam("hub.verify_token") String token,
                                         @RequestParam("hub.challenge") String challenge){
        if("subscribe".equals(mode)&&verifyToken.equals(token))return ResponseEntity.ok(challenge);
        return ResponseEntity.status(403).body("Forbidden");
    }
    @PostMapping
    public ApiResponse<Void> receive(@RequestBody JsonNode payload){
        webhookService.process(payload);
        return ApiResponse.success("Webhook received",null);
    }
}
