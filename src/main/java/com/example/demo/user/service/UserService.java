package com.example.demo.user.service;

import com.example.demo.S3.S3UploadService;
import com.example.demo.follow.repository.FollowRepository;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.user.dto.*;
import com.example.demo.user.domain.User;
import com.example.demo.user.exception.InvalidEmailException;
import com.example.demo.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final S3UploadService s3UploadService;

    @Transactional
    public Map<String,String> registerUser(UserSignupDTO userDTO, MultipartFile multipartFile) throws Exception {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new Exception("이메일 이미 있어요");
        }

// 로그 추가
        System.out.println("Registering user: " + userDTO.getEmail());
        System.out.println("Received profile file: " + multipartFile.getOriginalFilename());

        String url = s3UploadService.saveFile(multipartFile);

        User user = User.builder()
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .name(userDTO.getName())
                .profile(url)
                .intro(userDTO.getIntro())
                .build();

        userRepository.save(user);

        Map<String,String> map = new HashMap<>();
        map.put("jwtToken",jwtUtil.generateToken(String.valueOf(user.getId())));
        map.put("refreshToken",jwtUtil.generateRefreshToken(String.valueOf(user.getId())));

        return map;
    }

    @Transactional
    public Map<String,String> loginUser(UserLoginDTO userDTO) {
        if (!userRepository.existsByEmail(userDTO.getEmail())) {
            throw new InvalidEmailException("존재하는 않는 이메일");
        }
        User user = userRepository.findByEmail(userDTO.getEmail());
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new InvalidEmailException("비밀번호가 틀렸어요");
        }
        Map<String,String> map = new HashMap<>();
        map.put("jwtToken",jwtUtil.generateToken(String.valueOf(user.getId())));
        map.put("refreshToken",jwtUtil.generateRefreshToken(String.valueOf(user.getId())));

        return map;
    }


    @Transactional
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO updateDTO, MultipartFile profileFile) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updateDTO.getName() != null && !user.getName().equals(updateDTO.getName())) {
            user.nameUpdate(updateDTO.getName());
        }
        if (profileFile != null && !profileFile.isEmpty()) {
            if(user.getProfile() != null){
                s3UploadService.deleteFile(user.getProfile());
            }
            String url = s3UploadService.saveFile(profileFile);
            user.profileUpdate(url);
        }
        if (updateDTO.getIntro() != null) {
            user.introUpdate(updateDTO.getIntro());
        }
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()){
            user.passwordUpdate(passwordEncoder.encode(updateDTO.getPassword()));
        }
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getName())
                .profile(user.getProfile())
                .intro(user.getIntro())
                .build();
    }

    @Transactional
    public UserProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        long followerCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);

        return UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profile(user.getProfile())
                .intro(user.getIntro())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
    }



    @Transactional
    public UserGetDTO getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserGetDTO userDTO = new UserGetDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setIntro(user.getIntro());
        userDTO.setProfile(user.getProfile());
        return userDTO;
    }
}
