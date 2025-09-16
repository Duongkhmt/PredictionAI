
package com.example.DuDoanAI.service;

import com.example.DuDoanAI.model.User;
import com.example.DuDoanAI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    public User registerUser(@Valid User user) {
//        // Kiểm tra username đã tồn tại
//        if (userRepository.existsByUsername(user.getUsername())) {
//            throw new IllegalArgumentException("Tên người dùng đã tồn tại");
//        }
//
//        // Encode rawPassword → password
//        String encoded = passwordEncoder.encode(user.getRawPassword());
//        user.setPassword(encoded);
//
//        // Gán mặc định ROLE_USER nếu chưa có
//        if (user.getRole() == null || user.getRole().isEmpty()) {
//            user.setRole("USER");
//        }
//
//        // Gán enabled mặc định = true
//        user.setEnabled(true);
//
//        return userRepository.save(user);
//    }

    public User registerUser(@Valid User user) {
        // Kiểm tra username (email) đã tồn tại
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        // Encode rawPassword → password
        String encoded = passwordEncoder.encode(user.getRawPassword());
        user.setPassword(encoded);

        // Gán mặc định ROLE_USER nếu chưa có
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        user.setEnabled(true);

        return userRepository.save(user);
    }


    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setUsername(userDetails.getUsername());

        // Nếu có rawPassword mới → encode lại
        if (userDetails.getRawPassword() != null && !userDetails.getRawPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getRawPassword()));
        }

        user.setEnabled(userDetails.isEnabled());
        user.setRole(userDetails.getRole());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public long getUserCount() {
        return userRepository.count();
    }
}
