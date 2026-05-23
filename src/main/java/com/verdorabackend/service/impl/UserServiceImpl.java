package com.verdorabackend.service.impl;

import com.verdorabackend.dto.request.UpdateUserRequest;
import com.verdorabackend.dto.response.UserResponse;
import com.verdorabackend.entity.User;
import com.verdorabackend.exception.UserNotFoundException;
import com.verdorabackend.repository.UserRepository;
import com.verdorabackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setName(request.name());
        user.setPhoneNumber(request.phone());
        log.info("User updated: userId={}", userId);
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
        log.info("User deleted: userId={}", userId);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhoneNumber()
                ));
    }
}
