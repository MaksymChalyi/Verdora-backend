package com.verdorabackend.service;

public interface EmailService {

    void sendPasswordResetEmail(String to, String token);
}
