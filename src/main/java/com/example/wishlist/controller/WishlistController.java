package com.example.wishlist.controller;

import com.example.wishlist.model.Item;
import com.example.wishlist.service.ItemService;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;


import com.example.wishlist.model.Account;
import com.example.wishlist.model.Wishlist;
import com.example.wishlist.service.UserService;
import com.example.wishlist.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {
    @Autowired
    public WishlistService wishlistService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;


    @GetMapping("")
    public String showWishlists(HttpServletRequest request, Model model) {
        String userIdString = (String) request.getSession().getAttribute("userId");
        if (userIdString == null) {
            return "redirect:/login";
        }

        int userId = Integer.parseInt(userIdString);

        List<Wishlist> userWishlists = wishlistService.getWishlistsByUserId(userId);

        model.addAttribute("wishlists", userWishlists);

        return "wishlist";
    }


    @PostMapping(value = "/add")
    public String addWishlist(@RequestParam("wishlistName") String wishlistName, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String userIdString = (String) request.getSession().getAttribute("userId");
        if (userIdString == null) {
            return "redirect:/login";
        }

        try {
            Wishlist wishlist = new Wishlist();
            wishlist.setWishlistName(wishlistName);

            Wishlist addedWishlist = wishlistService.addWishlist(wishlist, userIdString);

            redirectAttributes.addFlashAttribute("successMessage", "Wishlist added successfully with ID: " + addedWishlist.getWishlistId());

            return "redirect:/wishlist";
        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());


            return "redirect:/wishlist";
        }
    }



    @GetMapping("/{wishlistId}")
    public String viewWishlistDetails(@PathVariable("wishlistId") int wishlistId, Model model) {
        Wishlist wishlist = wishlistService.getWishlistById(wishlistId);
        List<Item> items = itemService.getItemsByWishlistId(wishlistId); // Fetch items for the wishlist
        model.addAttribute("wishlist", wishlist);
        model.addAttribute("items", items); // Add items to the model
        return "wishlistDetails";
    }


    @PostMapping("/delete/{wishlistId}")
    public String deleteWishlist(@PathVariable int wishlistId, @RequestParam(required = false) List<Integer> itemId) {
        if (itemId != null) {
            wishlistService.deleteWishlist(wishlistId, itemId);
        }
        return "redirect:/wishlist";
    }


    @PostMapping("/update/{wishlistId}")
    public String updateWishlistName(@PathVariable int wishlistId, @RequestParam("newWishlistName") String newWishlistName, RedirectAttributes redirectAttributes) {
        try {
            boolean updated = wishlistService.updateWishlistName(wishlistId, newWishlistName);
            if (updated) {
                redirectAttributes.addFlashAttribute("successMessage", "Wishlist name updated successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update wishlist name.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/wishlist";
    }



}
