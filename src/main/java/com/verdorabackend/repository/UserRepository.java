package com.verdorabackend.repository;

import com.verdorabackend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByEmail(@NotBlank @Email @Size(max = 256) String email);

    boolean existsByEmail(String email);
}
