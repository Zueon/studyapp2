package com.zueon.springbootapp.account.application;

import com.zueon.springbootapp.account.domain.UserAccount;
import com.zueon.springbootapp.account.domain.entity.Account;
import com.zueon.springbootapp.account.endpoint.controller.SignUpForm;
import com.zueon.springbootapp.account.infra.AccountRepository;
import com.zueon.springbootapp.settings.controller.NotificationForm;
import com.zueon.springbootapp.settings.controller.Profile;
import com.zueon.springbootapp.tag.domain.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;


    public Account signUp(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateToken();
        sendVerificationEmail(newAccount);

        return newAccount;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .notificationSetting(Account.NotificationSetting.builder()
                        .studyCreatedByWeb(true)
                        .studyUpdatedByWeb(true)
                        .studyRegistrationResultByEmailByWeb(true)
                        .build())
                .build();

        Account newAccount = accountRepository.save(account);
        return newAccount;
    }

    public void sendVerificationEmail(Account newAccount){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("StudyApp 회원 가입 인증");

        mailMessage.setText(String.format("/check-email-token?token=%s&email=%s", newAccount.getEmailToken(), newAccount.getEmail()));

        javaMailSender.send(mailMessage);

    }

    public Account findAccountByEmail(String email){
        return accountRepository.findByEmail(email);

    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new UserAccount(account), account.getPassword(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);

    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = Optional.ofNullable(accountRepository.findByEmail(username))
                .orElse(accountRepository.findByNickname(username));

        if (account == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserAccount(account);
    }

    public void verify(Account account){
        account.verified();
        login(account);

    }

    public void updateProfile(Account account, Profile profile){
        account.updateProfile(profile);
        accountRepository.save(account);

    }

    public void updatePassword(Account account, String newPassword){
        account.updatePassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

    }

    public void updateNotification(Account account, NotificationForm notificationForm){
        account.updateNotification(notificationForm);
        accountRepository.save(account);

    }

    public void updateNickname(Account account, String nickname){
        account.updateNickname(nickname);
        accountRepository.save(account);
        login(account);

    }

    public void sendLoginLink(Account account){
        account.generateToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("로그인 링크");
        mailMessage.setText("/login-by-email?token=" + account.getEmailToken() + "&email=" + account.getEmail());

        javaMailSender.send(mailMessage);
    }

    public Set<Tag> getTags(Account account){
        return accountRepository.findById(account.getId()).orElseThrow().getTags();
    }

    public void addTag(Account account, Tag tag) {
        accountRepository.findById(account.getId())
                .ifPresent(a -> a.getTags().add(tag));

    }

    public void removeTag(Account account, Tag tag){
        accountRepository.findById(account.getId())
                .map(Account::getTags)
                .ifPresent(tags -> tags.remove(tag));
    }
}
