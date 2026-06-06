package com.ph.core.story.user.interfaces;

import com.ph.core.lib.query.model.QueryRequest;
import com.ph.core.story.user.application.command.EmailVerificationService;
import com.ph.core.story.user.application.command.ForgotPasswordService;
import com.ph.core.story.user.application.command.UserCrudService;
import com.ph.core.story.user.application.command.UserRegisterService;
import com.ph.core.story.user.application.command.dto.EmailOtpStatusResponse;
import com.ph.core.story.user.application.command.dto.ForgotPasswordOtpStatusResponse;
import com.ph.core.story.user.application.command.dto.ForgotPasswordResetRequest;
import com.ph.core.story.user.application.command.dto.ForgotPasswordSendOtpRequest;
import com.ph.core.story.user.application.command.dto.ForgotPasswordVerifyOtpRequest;
import com.ph.core.story.user.application.command.dto.ForgotPasswordVerifyOtpResponse;
import com.ph.core.story.user.application.command.dto.ResetPasswordRequest;
import com.ph.core.story.user.application.command.dto.UserCreateRequest;
import com.ph.core.story.user.application.command.dto.UserRegisterRequest;
import com.ph.core.story.user.application.command.dto.UserUpdateRequest;
import com.ph.core.story.user.application.command.dto.VerifyEmailOtpRequest;
import com.ph.core.story.user.application.query.UserSearchService;
import com.ph.core.story.user.application.query.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: thêm quyền cho các API
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCrudService crudService;
    private final UserSearchService searchService;
    private final UserRegisterService userRegisterService;
    private final EmailVerificationService emailVerificationService;
    private final ForgotPasswordService forgotPasswordService;

    @PostMapping
    public UserResponse create(@RequestBody @Valid UserCreateRequest request) {
        return crudService.create(request);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request) {
        return crudService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        crudService.delete(id);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getClaimAsString("user_id"));
        return searchService.findById(userId);
    }

    /**
     * Lấy thông tin người dùng theo id
     * 
     * @param id
     * @return
     */
    // @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return searchService.findById(id);
    }

    @GetMapping
    public Page<UserResponse> findAll(Pageable pageable) {
        return searchService.findAll(pageable);
    }

    @PostMapping("/search")
    public Page<UserResponse> search(@RequestBody QueryRequest request) {
        return searchService.search(request);
    }

    /**
     * =================================================================================
     * 
     * CÁC THAO TÁC CƠ BẢN TRÊN NGƯỜI DÙNG
     * 
     * =================================================================================
     */

    /**
     * Đăng ký người dùng
     * 
     * @param request
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserRegisterRequest request) {
        userRegisterService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Buoc 1 cua man hinh xac thuc: gui OTP den email cua user dang login.
     */
    @PostMapping("/me/email-verification/send-otp")
    public EmailOtpStatusResponse sendEmailVerificationOtp(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getClaimAsString("user_id"));
        return emailVerificationService.sendOtp(userId);
    }

    /**
     * Buoc 2 cua man hinh xac thuc: neu OTP dung thi doi emailVerified thanh true.
     */
    @PostMapping("/me/email-verification/verify")
    public ResponseEntity<Void> verifyEmailOtp(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody VerifyEmailOtpRequest request) {
        Long userId = Long.valueOf(jwt.getClaimAsString("user_id"));
        emailVerificationService.verifyOtp(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password-reset/send-otp")
    public ForgotPasswordOtpStatusResponse sendForgotPasswordOtp(
            @Valid @RequestBody ForgotPasswordSendOtpRequest request) {
        return forgotPasswordService.sendOtp(request);
    }

    @PostMapping("/password-reset/verify-otp")
    public ForgotPasswordVerifyOtpResponse verifyForgotPasswordOtp(
            @Valid @RequestBody ForgotPasswordVerifyOtpRequest request) {
        return forgotPasswordService.verifyOtp(request);
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Void> confirmForgotPassword(
            @Valid @RequestBody ForgotPasswordResetRequest request) {
        forgotPasswordService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reset mật khẩu
     * 
     * @param jwt
     * @param request
     * @return
     */
    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ResetPasswordRequest request) {
        crudService.resetPassword(jwt.getSubject(), request);
        return ResponseEntity.noContent().build();
    }
}
