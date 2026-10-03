package com.verdorabackend.service;

import com.verdorabackend.dto.request.UpdateUserRequest;
import com.verdorabackend.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse updateCurrentUser(String email, UpdateUserRequest request);

    UserResponse getUserByEmail(String email);

    void deleteCurrentUser(String email, String password);

    Page<UserResponse> getAllUsers(Pageable pageable);
}
