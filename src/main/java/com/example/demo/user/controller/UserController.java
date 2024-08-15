package com.example.demo.user.controller;

import com.example.demo.jwt.JwtUtil;
import com.example.demo.user.dto.*;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/main")
    public ModelAndView goMain2(ModelAndView modelAndView) {
        modelAndView.setViewName("main");
        return modelAndView;
    }

    @GetMapping("/")
    public ModelAndView goMain(ModelAndView modelAndView) {
        modelAndView.setViewName("main");
        return modelAndView;
    }


    @GetMapping("/login")
    public ModelAndView login(ModelAndView modelAndView) {
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO userDTO, HttpServletResponse response) {

        Map<String,String> map = userService.loginUser(userDTO);

        Cookie cookie = new Cookie("stockJwtToken", map.get("jwtToken"));
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setPath("/");

        // 리프레시 토큰을 쿠키에 저장
        Cookie refreshTokenCookie = new Cookie("stockRefreshToken", map.get("refreshToken"));
        refreshTokenCookie.setHttpOnly(false);
        refreshTokenCookie.setSecure(false); // HTTPS에서만 사용
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        refreshTokenCookie.setPath("/");

        response.addCookie(refreshTokenCookie);
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/main") // 리다이렉트할 URL
                .build();
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("stockJwtToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return "redirect:/main";
    }



    @GetMapping("/update")
    public String showUpdateForm(HttpServletRequest request, Model model) {

        String token = request.getHeader("Authorization").substring(7);
        model.addAttribute("user", userService.getUser(token));

        return "editUser";
    }

    @PostMapping("/update")
    public String updateUser(@RequestPart("userDTO") UserSignupDTO userDTO,
                             @RequestPart("profile") MultipartFile profileFile) throws Exception {
        UserResponseDTO user = userService.updateUser(userDTO,profileFile);

        return "redirect:/user/edit/" + user.getId();
    }
}
