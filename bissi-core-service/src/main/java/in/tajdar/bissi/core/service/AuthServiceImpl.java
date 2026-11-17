package in.tajdar.bissi.core.service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.tajdar.bissi.core.dto.request.AuthResponse;
import in.tajdar.bissi.core.dto.request.LoginRequest;
import in.tajdar.bissi.core.dto.request.RegisterRequest;
import in.tajdar.bissi.core.entity.Diary;
import in.tajdar.bissi.core.entity.User;
import in.tajdar.bissi.core.enums.DiaryStatus;
import in.tajdar.bissi.core.enums.Role;
import in.tajdar.bissi.core.exception.BusinessException;
import in.tajdar.bissi.core.exception.ResourceNotFoundException;
import in.tajdar.bissi.core.repo.DiaryRepo;
import in.tajdar.bissi.core.repo.UserRepo;
import in.tajdar.bissi.core.security.CustomUserDetailsService;
import in.tajdar.bissi.core.security.JwtService;
import lombok.RequiredArgsConstructor;
@Service @RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
	private final UserRepo userRepository;
    private final DiaryRepo diaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email is already registered: " + request.email());
        }
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new BusinessException("Phone number is already registered: " + request.phoneNumber());
        }

        Role assignedRole = (request.role() != null) ? request.role() : Role.ROLE_MEMBER;

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .password(passwordEncoder.encode(request.password()))
                .role(assignedRole)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        String diaryNumber = null;
        String diaryStatus = null;

        // Agar Member hai toh auto-generate PENDING Diary
        if (assignedRole == Role.ROLE_MEMBER) {
            String generatedDiaryNo = "BISSI-" + Year.now().getValue() + "-"
                    + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

            Diary diary = Diary.builder()
                    .diaryNumber(generatedDiaryNo)
                    .member(savedUser)
                    .status(DiaryStatus.PENDING)
                    .currentBalance(BigDecimal.ZERO)
                    .build();

            Diary savedDiary = diaryRepository.save(diary);
            diaryNumber = savedDiary.getDiaryNumber();
            diaryStatus = savedDiary.getStatus().name();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtService.generateToken(userDetails, savedUser.getRole().name(), savedUser.getId());

        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .diaryNumber(diaryNumber)
                .diaryStatus(diaryStatus)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails, user.getRole().name(), user.getId());

        Diary diary = diaryRepository.findByMemberId(user.getId()).orElse(null);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .diaryNumber(diary != null ? diary.getDiaryNumber() : null)
                .diaryStatus(diary != null ? diary.getStatus().name() : null)
                .build();
    }
}
