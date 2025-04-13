package com.demo.chatApp.keyManager;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/Secure/api")
public class SecureDataController {

    private final KeyManager keyManager;

    public SecureDataController(KeyManager keyManager){
        this.keyManager=keyManager;
    }

    @PostMapping("/secure-data")
    public String handleSecureData(@RequestBody Map<String, String> body){
        String encryptedBase64=body.get("encrypted");
        byte [] encryptedBytes= Base64.getDecoder().decode(encryptedBase64);

        try{
            Cipher cipher= Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, keyManager.getPrivateKey());
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String decrypted = new String(decryptedBytes);
            System.out.println("Decrypted successfully: " + decrypted);
            return  decrypted;
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e){
            throw new RuntimeException("Failed to handle secure data",e);
        }

    }
}
