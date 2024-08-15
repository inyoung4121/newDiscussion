package com.example.demo.user.service;

import com.example.demo.user.domain.Certification;
import com.example.demo.user.exception.InvalidEmailException;
import com.example.demo.user.repository.CertificationRepository;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final CertificationRepository certificationRepository;
    private final UserRepository userRepository;

    public void sendVerificationEmail(String toEmail){
        if (!isValidEmail(toEmail)) {
            throw new InvalidEmailException( "유효하지 않은 이메일 형식입니다.");
        }

        if (userRepository.existsByEmail(toEmail)) {
            throw new InvalidEmailException( "이미 존재하는 이메일입니다.");
        }

        String certificationNum = getRandomNumber();
        String subject = "인증번호 메일 왔어요";
        String message = "인증번호를 입력해주세요\n" + certificationNum;

        Certification certification = Certification.builder()
                .email(toEmail)
                .CertificationStatus(false)
                .CertificationNumber(certificationNum)
                .build();

        certificationRepository.save(certification);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }

    public boolean verifyCode(String email, String code) {
        Certification certification = certificationRepository.findByEmail(email);
        return certification != null && certification.getCertificationNumber().equals(code);
    }

    public String getRandomNumber(){
        Random random = new Random();
        return "" + 100000 + random.nextInt(900000);
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }


}
