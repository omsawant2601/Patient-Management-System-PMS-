package com.cliniccare.backend.whatsapp;

import com.cliniccare.backend.common.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.security.MessageDigest;

@RestController @RequestMapping("/api/v1/webhooks/whatsapp")
public class WhatsAppWebhookController {
    @Value("${whatsapp.verify-token:}") private String verifyToken;
    private final WhatsAppWebhookService webhookService;
    private final WhatsAppSignatureVerifier signatureVerifier;
    private final ObjectMapper objectMapper=new ObjectMapper();

    public WhatsAppWebhookController(WhatsAppWebhookService webhookService,
                                     WhatsAppSignatureVerifier signatureVerifier){
        this.webhookService=webhookService;this.signatureVerifier=signatureVerifier;
    }

    @GetMapping
    public ResponseEntity<String> verify(@RequestParam("hub.mode") String mode,
                                         @RequestParam("hub.verify_token") String token,
                                         @RequestParam("hub.challenge") String challenge){
        if(verifyToken==null||verifyToken.isBlank())
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Webhook verification is not configured");
        if("subscribe".equals(mode)&&MessageDigest.isEqual(verifyToken.getBytes(StandardCharsets.UTF_8),
                token.getBytes(StandardCharsets.UTF_8)))return ResponseEntity.ok(challenge);
        return ResponseEntity.status(403).body("Forbidden");
    }
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> receive(@RequestBody byte[] body,
            @RequestHeader(name="X-Hub-Signature-256",required=false) String signature){
        if(!signatureVerifier.isConfigured())
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("Webhook signature verification is not configured",null));
        if(!signatureVerifier.isValid(body,signature))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid webhook signature",null));
        try {
            JsonNode payload=objectMapper.readTree(body);
            if(payload==null||!payload.isObject())
                return ResponseEntity.badRequest().body(ApiResponse.error("Invalid webhook payload",null));
            webhookService.process(payload);
            return ResponseEntity.ok(ApiResponse.success("Webhook received",null));
        } catch(IOException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid webhook payload",null));
        }
    }
}
