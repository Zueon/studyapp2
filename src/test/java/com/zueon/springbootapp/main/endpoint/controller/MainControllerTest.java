package com.zueon.springbootapp.main.endpoint.controller;

import com.zueon.springbootapp.account.application.AccountService;
import com.zueon.springbootapp.account.endpoint.controller.SignUpForm;
import com.zueon.springbootapp.account.infra.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("hiho070102");
        signUpForm.setEmail("hiho070102@naver.com");
        signUpForm.setPassword("1234!@#$");

        accountService.signUp(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일로 로그인 : 성공")
    public void loginWithEmail() throws Exception {

        mockMvc.perform(post("/login")
                        .param("username", "hiho070102@naver.com")
                        .param("password", "1234!@#$")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("hiho070102"));
    }

    @Test
    @DisplayName("닉네임으로 로그인 : 성공")
    public void loginWithNickname() throws Exception {

        mockMvc.perform(post("/login")
                        .param("username", "hiho070102")
                        .param("password", "1234!@#$")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("hiho070102"));
    }


}
