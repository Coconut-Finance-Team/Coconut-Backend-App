package com.coconut.stock_app.service;

public interface EmailService {
    void sendVerificationEmail(String to);

    boolean verifyEmail(String email, String code);

    boolean isEmailVerified(String email);

    void sendTemporaryPassword(String to, String temporaryPassword);

    void setGoogleEmailVerified(String email);
}