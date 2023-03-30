package com.zueon.springbootapp.settings.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zueon.springbootapp.account.application.AccountService;
import com.zueon.springbootapp.account.domain.entity.Account;
import com.zueon.springbootapp.account.domain.support.CurrentUser;
import com.zueon.springbootapp.settings.controller.validator.NicknameFormValidator;
import com.zueon.springbootapp.settings.controller.validator.PasswordValidtor;
import com.zueon.springbootapp.tag.domain.entity.Tag;
import com.zueon.springbootapp.tag.infra.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingsController {
    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/" + SETTINGS_PROFILE_VIEW_NAME;

    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/" + SETTINGS_PASSWORD_VIEW_NAME;

    static final String SETTINGS_NOTIFICATION_VIEW_NAME = "settings/notification";
    static final String SETTINGS_NOTIFICATION_URL = "/" + SETTINGS_NOTIFICATION_VIEW_NAME;

    static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
    static final String SETTINGS_ACCOUNT_URL = "/" + SETTINGS_ACCOUNT_VIEW_NAME;

    static final String SETTINGS_TAGS_VIEW_NAME = "settings/tags";
    static final String SETTINGS_TAGS_URL = "/" + SETTINGS_TAGS_VIEW_NAME;

    private final AccountService accountService;
    private final PasswordValidtor passwordValidtor;
    private final NicknameFormValidator nicknameFormValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;

    @InitBinder("passwordForm")
    public void setPasswordValidtor(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordValidtor);
    }

    @InitBinder("nicknameForm")
    public void setNicknameFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameFormValidator);
    }

    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(Profile.from(account));

        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model, RedirectAttributes attributes){
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필을 수정하였습니다");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String passUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors, Model model, RedirectAttributes attributes){
        if (errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "패스워드를 변경하였습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;

    }

    @GetMapping(SETTINGS_NOTIFICATION_URL)
    public String notificationForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(NotificationForm.from(account));
        return SETTINGS_NOTIFICATION_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATION_URL)
    public String updateNotification(@CurrentUser Account account, @Valid NotificationForm notificationForm, Model model, RedirectAttributes attributes, Errors errors){
        if (errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_NOTIFICATION_URL;
        }
        accountService.updateNotification(account, notificationForm);
        attributes.addFlashAttribute("message", "알림 설정을 수정하였습니다.");

        return "redirect:" + SETTINGS_NOTIFICATION_URL;
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String nicknameForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new NicknameForm(account.getNickname()));
        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String updateNickname(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Model model, RedirectAttributes attributes, Errors errors){
        if (errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }
        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message", "닉네임을 수정하였습니다.");

        return "redirect:" + SETTINGS_ACCOUNT_URL;
    }

    @GetMapping(SETTINGS_TAGS_URL)
    public String updateTags(@CurrentUser Account account, Model model) {
        model.addAttribute(account);

        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags",
                tags.stream()
                        .map(Tag::getTitle)
                        .collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll()
                .stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList());

        String whitelist = null;

        try {
            whitelist = objectMapper.writeValueAsString(allTags);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        model.addAttribute("whitelist", whitelist);
        return SETTINGS_TAGS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_TAGS_URL + "/add")
    @ResponseStatus(HttpStatus.OK)
    public void addTag(@CurrentUser Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title)
                .orElseGet(()->tagRepository.save(Tag.builder().title(title).build()));
        accountService.addTag(account, tag);
    }

    @PostMapping(SETTINGS_TAGS_URL + "/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title)
                .orElseThrow(IllegalArgumentException::new);

        accountService.removeTag(account, tag);
    }



}
