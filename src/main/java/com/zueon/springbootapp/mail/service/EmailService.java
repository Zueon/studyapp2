package com.zueon.springbootapp.mail.service;

import com.zueon.springbootapp.mail.EmailMessage;

public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
