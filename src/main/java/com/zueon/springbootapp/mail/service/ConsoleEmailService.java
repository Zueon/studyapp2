package com.zueon.springbootapp.mail.service;

import com.zueon.springbootapp.mail.EmailMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("local")
@Service
@Log4j2
public class ConsoleEmailService implements EmailService{
    @Override
    public void sendEmail(EmailMessage emailMessage) {
       log.info("sent email : {}", emailMessage.getMessage() );
    }
}
