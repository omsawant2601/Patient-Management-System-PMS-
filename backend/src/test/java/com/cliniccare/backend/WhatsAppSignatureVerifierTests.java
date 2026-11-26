package com.cliniccare.backend;

import com.cliniccare.backend.whatsapp.WhatsAppSignatureVerifier;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class WhatsAppSignatureVerifierTests {
    @Test
    void acceptsOnlyTheCorrectPayloadSignature() throws Exception {
        String secret="test-app-secret";
        byte[] body="{\"object\":\"whatsapp_business_account\"}".getBytes(StandardCharsets.UTF_8);
        Mac mac=Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));
        String signature="sha256="+HexFormat.of().formatHex(mac.doFinal(body));
        WhatsAppSignatureVerifier verifier=new WhatsAppSignatureVerifier(secret);

        assertTrue(verifier.isValid(body,signature));
        assertFalse(verifier.isValid(body,"sha256=00"));
        assertFalse(verifier.isValid(body,null));
    }

    @Test
    void rejectsWebhookPostsWhenAppSecretIsNotConfigured() {
        WhatsAppSignatureVerifier verifier=new WhatsAppSignatureVerifier("");
        assertFalse(verifier.isConfigured());
        assertFalse(verifier.isValid(new byte[0],"sha256=00"));
    }
}
