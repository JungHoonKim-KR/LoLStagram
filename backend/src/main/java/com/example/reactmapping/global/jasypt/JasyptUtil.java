package com.example.reactmapping.global.jasypt;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JasyptUtil {

    private StringEncryptor jasyptStringEncryptor;

    @Autowired
    public void EncryptionUtil(StringEncryptor jasyptStringEncryptor) {
        this.jasyptStringEncryptor = jasyptStringEncryptor;
    }
    public String encrypt(String message) {
        return jasyptStringEncryptor.encrypt(message);
    }

    public String decrypt(String encryptedMessage) {
        return jasyptStringEncryptor.decrypt(encryptedMessage);
    }
}