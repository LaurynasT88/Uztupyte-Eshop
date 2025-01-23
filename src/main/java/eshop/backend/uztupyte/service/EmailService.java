package eshop.backend.uztupyte.service;

import eshop.backend.uztupyte.api.model.VerificationToken;
import eshop.backend.uztupyte.exception.EmailFailureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${email.from}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String url;

    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    private SimpleMailMessage makeMailMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAddress);
        return simpleMailMessage;
    }

    public void sendVerificationEmail (VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(verificationToken.getCustomer().getEmail());
        message.setSubject("Verify your email to active your account");
        message.setText("Please follow the link below to verify your email to activate your account. \n" + url + "/auth/verify?token=" + verificationToken.getToken()); //TODO ADD WHEN FRONT END IS DONE
        try{
                javaMailSender.send(message);
                }catch (MailException ex){
            throw new EmailFailureException();
        }
    }


}
