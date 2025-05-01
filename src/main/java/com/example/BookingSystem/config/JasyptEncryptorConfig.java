package com.example.BookingSystem.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptEncryptorConfig {
    private String password = "Booking-JASYPT-System";

    @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

    public String encryptMessage(String plainText) {
        var encryptor = stringEncryptor();
        return encryptor.encrypt(plainText);
    }

    public String decryptMessage(String encryptedText) {
        var encryptor = stringEncryptor();
        return encryptor.decrypt(encryptedText);
    }

//    public static void main(String[] args) {
//        JasyptEncryptorConfig encryptConfig = new JasyptEncryptorConfig();
//        String plainText = "lcSP9PHpgyn5Q48HTIcSlJ9LJv2zQdCprAeNM8gHWUk=";
//        String encryptedString = encryptConfig.encryptMessage(plainText);
//        System.out.println("Encrypted String : " + encryptedString);
//
//        String encryptText = encryptedString;
//        String decryptString = encryptConfig.decryptMessage(encryptText);
//        System.out.println("Decrypted String : " + decryptString);
//
//    }

//    public static void main(String[] args) {
//        JasyptEncryptorConfig encryptConfig = new JasyptEncryptorConfig();
//        String plainUsername = "admin";
//        String plainPassword = "admin123";
//        String encryptedUsername = encryptConfig.encryptMessage(plainUsername);
//        String encryptedPassword = encryptConfig.encryptMessage(plainPassword);
//        System.out.println("Encrypted Username: " + encryptedUsername);
//        System.out.println("Encrypted Password: " + encryptedPassword);
//    }

}
