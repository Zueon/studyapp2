package com.zueon.springbootapp.account.application;

import com.zueon.springbootapp.account.domain.UserAccount;
import com.zueon.springbootapp.account.domain.entity.Account;
import com.zueon.springbootapp.account.domain.entity.Zone;
import com.zueon.springbootapp.account.endpoint.controller.SignUpForm;
import com.zueon.springbootapp.account.infra.AccountRepository;
import com.zueon.springbootapp.config.AppProperties;
import com.zueon.springbootapp.mail.EmailMessage;
import com.zueon.springbootapp.mail.service.EmailService;
import com.zueon.springbootapp.settings.controller.form.NotificationForm;
import com.zueon.springbootapp.settings.controller.Profile;
import com.zueon.springbootapp.tag.domain.entity.Tag;
import com.zueon.springbootapp.zone.infra.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final ZoneRepository zoneRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;


    public Account signUp(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        sendVerificationEmail(newAccount);

        return newAccount;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.with(
                signUpForm.getEmail(),
                signUpForm.getNickname(),
                passwordEncoder.encode(signUpForm.getPassword()));
        account.generateToken();
        return accountRepository.save(account);

    }

    public void sendVerificationEmail(Account newAccount){
        Context context = new Context();
        context.setVariable("link", String.format("/check-email-token?token=%s&email=%s"
                ,newAccount.getEmailToken(), newAccount.getEmail()));
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message","StudyApp 가입 인증을 위해 링크를 클릭하세요");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);

        emailService.sendEmail(
                EmailMessage.builder()
                        .to(newAccount.getEmail())
                        .subject("StudyApp 회원 가입 인증")
                        .message(message)
                        .build()

        );

    }

    public Account findAccountByEmail(String email){
        return accountRepository.findByEmail(email);

    }

    public Account getAccountBy(String nickname) {
        return Optional.ofNullable(accountRepository.findByNickname(nickname))
                .orElseThrow(() -> new IllegalArgumentException(nickname + "에 해당하는 사용자가 존재하지 않습니다."));
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
        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailToken() + "&email=" + account.getEmail());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", "StudyApp 로그인하기");
        context.setVariable("message","로그인하려면 아래 링크를 클릭하세요");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);

        account.generateToken();
        emailService.sendEmail(
                EmailMessage.builder()
                        .to(account.getEmail())
                        .subject("[StudyApp] 로그인 링크")
                        .message(message)
                        .build()
        );
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

    public Set<Zone> getZones(Account account){
        return  accountRepository.findById(account.getId())
                .orElseThrow()
                .getZones();

    }

    public void addZone(Account account, Zone zone) {
        accountRepository.findById(account.getId())
                .ifPresent(a -> a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone){
        accountRepository.findById(account.getId())
                .ifPresent(a -> a.getZones().remove(zone));

    }


}
