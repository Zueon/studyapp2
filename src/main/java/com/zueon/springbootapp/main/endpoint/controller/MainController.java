package com.zueon.springbootapp.main.endpoint.controller;

import com.zueon.springbootapp.account.domain.entity.Account;
import com.zueon.springbootapp.account.domain.support.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){
        if (account != null) {
            model.addAttribute(account);
        }
        return "index";

    }

    @GetMapping("/login")
    public String login(){
        return "login";

    }
}
