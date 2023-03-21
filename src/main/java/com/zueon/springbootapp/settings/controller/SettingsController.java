package com.zueon.springbootapp.settings.controller;

import com.zueon.springbootapp.account.domain.entity.Account;
import com.zueon.springbootapp.account.domain.support.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController {
    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(Profile.from(account));

        return "settings/profile";
    }
}
