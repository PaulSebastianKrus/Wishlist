package com.example.wishlist.controller;

import com.example.wishlist.model.Account;
import com.example.wishlist.service.UserService;
import com.example.wishlist.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class UserController {


    @Autowired
    public WishlistService wishlistService;

    @Autowired
    private UserService userService;



    @GetMapping("/")
    public RedirectView redirectToLogin() {
        return new RedirectView("/login");
    }


    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerAccount(Account account) {
        userService.saveAccount(account);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, @RequestParam("username") String username, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        if (userService.isValidUser(username, password)) {
            String userId = userService.getUserIdByUsername(username);
            request.getSession().setAttribute("userId", userId);
            return "redirect:/wishlist";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid login");
            return "redirect:/login";
        }
    }


}
