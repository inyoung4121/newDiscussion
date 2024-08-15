package com.example.demo.user.controller;

import com.example.demo.user.dto.UserDTO;
import com.example.demo.user.dto.UserSignupDTO;
import com.example.demo.user.service.EmailService;
import com.example.demo.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class SignupController {

    private final EmailService emailService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping("/signup")
    public ModelAndView showSignupForm(ModelAndView model) {
        model.addObject("userDTO", new UserDTO());
        model.setViewName("signup");
        return model;
    }

    @PostMapping("/send-verification-email")
    public ResponseEntity<Map<String, String>> sendVerificationEmail(@RequestParam String email) {

        emailService.sendVerificationEmail(email);

        Map<String, String> response = new HashMap<>();
        response.put("redirect", "/signup");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestParam("email") String email, @RequestParam("code") String code) {
        Map<String, String> response = new HashMap<>();
        if (emailService.verifyCode(email, code)) {
            response.put("emailVerified", "true");
            response.put("successMessage", "Email verified! You can now complete your registration.");
        } else {
            response.put("emailVerified", "false");
            response.put("errorMessage", "Invalid verification code.");
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping("/signup")
    public String signup(@RequestPart("userDTO") UserSignupDTO userDTO,
                         @RequestPart("profile") MultipartFile profileFile,
                         HttpServletResponse response) throws Exception {

        // 유저 등록 로직
        String jwtToken = userService.registerUser(userDTO, profileFile);

        // 쿠키 설정
        Cookie cookie = new Cookie("stockJwtToken", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        return "main";  // main 페이지로 리다이렉트
    }
}
