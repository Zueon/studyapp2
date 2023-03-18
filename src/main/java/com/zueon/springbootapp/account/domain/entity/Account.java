package com.zueon.springbootapp.account.domain.entity;

import com.zueon.springbootapp.account.domain.support.ListStringConverter;
import com.zueon.springbootapp.domain.entity.AuditingEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter @ToString
public class Account extends AuditingEntity {

    @Id @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean isValid;

    private String emailToken;

    @Embedded
    private Profile profile;

    @Embedded
    private NotificationSetting notificationSetting;

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    @Getter
    @ToString
    public static class Profile {
        private String bio;
        @Convert(converter = ListStringConverter.class)
        private List<String> url;
        private String job;
        private String location;
        private String company;
        @Lob
        @Basic(fetch = FetchType.EAGER)
        private String image;
    }

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder @Getter @ToString
    public static class NotificationSetting {
        private boolean studyCreatedByEmail;
        private boolean studyCreatedByWeb;
        private boolean studyRegistrationResultByEmailByEmail;
        private boolean studyRegistrationResultByEmailByWeb;
        private boolean studyUpdatedByEmail;
        private boolean studyUpdatedByWeb;
    }

    private LocalDateTime joinedAt;

    private LocalDateTime emailTokenGeneratedAt;

    public void generateToken(){
        this.emailToken = UUID.randomUUID().toString();

    }

    public void verified(){
        this.isValid = true;
        joinedAt = LocalDateTime.now();

    }

    public boolean enableToSendEmail(){
        return this.emailTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(5));

    }
}
