package com.demo.chatApp.keyManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/KeyManager/api")
public class PublicKeyController {

    private final KeyManager keyManager;

    public PublicKeyController(KeyManager keyManager) {
        this.keyManager = keyManager;
    }

    @GetMapping("/public-key")
    public String getPublicKey(){
        PublicKey publicKey = keyManager.getPublicKey();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());

    }

    @GetMapping("/admin/generateNewKeys")
    public ResponseEntity<String> generateNewKeyPair() {
        try {
            keyManager.generateKeyPair();
            return ResponseEntity.ok("New key pair generated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Key generation failed: " + e.getMessage());
        }
    }

}
