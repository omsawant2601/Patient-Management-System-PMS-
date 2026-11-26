package com.cliniccare.backend.whatsapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class WhatsAppSignatureVerifier {
    private final String appSecret;

    public WhatsAppSignatureVerifier(@Value("${whatsapp.app-secret:}") String appSecret) {
        this.appSecret=appSecret;
    }

    public boolean isConfigured() {
        return appSecret!=null&&!appSecret.isBlank();
    }

    public boolean isValid(byte[] payload,String signature) {
        if(!isConfigured()||signature==null||!signature.startsWith("sha256="))return false;
        try {
            byte[] supplied=HexFormat.of().parseHex(signature.substring("sha256=".length()));
            Mac mac=Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));
            return MessageDigest.isEqual(mac.doFinal(payload),supplied);
        } catch(IllegalArgumentException ex) {
            return false;
        } catch(NoSuchAlgorithmException|InvalidKeyException ex) {
            throw new IllegalStateException("Could not verify WhatsApp webhook signature",ex);
        }
    }
}
