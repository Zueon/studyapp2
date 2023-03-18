package com.zueon.springbootapp.account.endpoint.controller;

import com.zueon.springbootapp.account.domain.entity.Account;
import com.zueon.springbootapp.account.infra.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @MockBean
    JavaMailSender mailSender;

    @Test
    @DisplayName("회원가입 화면 진입 확인")
    public void signupForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @Test
    @DisplayName("회원 가입 처리: 입력값 오류")
    void signUpSubmitWithError() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "nickname")
                        .param("email", "email@gmail")
                        .param("password", "1234!")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @Test
    @DisplayName("회원 가입 처리: 입력값 정상")
    void signUpSubmit() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "nickname")
                        .param("email", "email@gmail.com")
                        .param("password", "1234!@#$")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        assertTrue(accountRepository.existsByEmail("email@gmail.com")); // 메일이 DB에 저장되었는지 확인

        then(mailSender)
                .should()
                .send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("인증 메일 확인 : 잘못된 링크")
    public void verifyEmailWithWrongLink() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("token", "token")
                .param("email", "email"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/email-verification"))
                .andExpect(model().attributeExists("error"));

    }

    @Test
    @DisplayName("인증 메일 확인 : 유효한 링크")
    @Transactional
    public void verifyEmail() throws Exception {
        Account account = Account.builder() // (2)
                .email("email@email.com")
                .password("1234!@#$")
                .nickname("nickname")
                .notificationSetting(Account.NotificationSetting.builder()
                        .studyCreatedByWeb(true)
                        .studyUpdatedByWeb(true)
                        .studyRegistrationResultByEmailByWeb(true)
                        .build())
                .build();

        Account newAccount = accountRepository.save(account); // (3)
        newAccount.generateToken(); // (4)
        mockMvc.perform(get("/check-email-token")
                        .param("token", newAccount.getEmailToken()) // (5)
                        .param("email", newAccount.getEmail())) // (5)
                .andExpect(status().isOk()) // (6)
                .andExpect(view().name("account/email-verification")) // (6)
                .andExpect(model().attributeDoesNotExist("error")) // (7)
                .andExpect(model().attributeExists("numberOfUsers", "nickname"))
                .andExpect(authenticated().withUsername("nickname"));
    }

}