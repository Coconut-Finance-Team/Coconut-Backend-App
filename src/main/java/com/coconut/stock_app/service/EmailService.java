package com.coconut.stock_app.service;

public interface EmailService {
    void sendVerificationEmail(String to);
    boolean verifyEmail(String email, String code);
}