package com.boilerplate.boilerplate.domain.user.controller;

import com.boilerplate.boilerplate.domain.user.dto.EmailDuplicateCheckResponse;
import com.boilerplate.boilerplate.domain.user.dto.PublicUserResponse;
import com.boilerplate.boilerplate.domain.user.dto.UpdateUserProfileRequest;
import com.boilerplate.boilerplate.domain.user.dto.UserResponse;
import com.boilerplate.boilerplate.domain.user.dto.UsernameDuplicateCheckResponse;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/my")
    public ResponseEntity<UserResponse> getUserProfile() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserProfile());
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserResponse> updateUserProfile(
        @PathVariable String username,
        @RequestBody UpdateUserProfileRequest updateRequest
    ) {
        UserResponse userResponse = userService.updateUserProfile(username, updateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<EmailDuplicateCheckResponse> checkEmailDuplicate(
        @PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.checkEmailDuplicate(email));
    }

    @GetMapping("/check/username/{username}")
    public ResponseEntity<UsernameDuplicateCheckResponse> checkUsernameDuplicate(
        @PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.checkUsernameDuplicate(username));
    }

    @GetMapping("/{username}")
    public ResponseEntity<PublicUserResponse> getPublicUserByUsername(
        @PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.getPublicUserByUsername(username));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteByUsername(username);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/{username}/image")
//    public ResponseEntity<Void> uploadProfileImage(
//        @PathVariable String username,
//        @RequestPart("image") MultipartFile file
//    ) {
//        userService.uploadProfileImage(username, file);
//        return ResponseEntity.ok().build();
//    }
}
