
package com.example.DuDoanAI.controller;

import com.example.DuDoanAI.model.User;
import com.example.DuDoanAI.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        System.out.println("=== REGISTER REQUEST DEBUG ===");
        System.out.println("Username: " + user.getUsername());
        System.out.println("RawPassword: " + (user.getRawPassword() != null ? "[LENGTH: " + user.getRawPassword().length() + "]" : "null"));
        System.out.println("Has validation errors: " + result.hasErrors());

        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                System.out.println("- Field: " + error.getField() + ", Error: " + error.getDefaultMessage());
            }

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", result);
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin đăng ký");
            return "redirect:/register";
        }

        try {
            User savedUser = userService.registerUser(user);
            System.out.println("User registered successfully: " + savedUser.getId());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng ký thành công! Tài khoản " + user.getUsername() + " đã được tạo.");
            return "redirect:/login";

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên người dùng đã tồn tại!");
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/register";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi xảy ra khi đăng ký: " + e.getMessage());
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/register";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
