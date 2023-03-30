package com.zueon.springbootapp.account.domain.entity;

import com.zueon.springbootapp.domain.entity.AuditingEntity;
import com.zueon.springbootapp.settings.controller.NotificationForm;
import com.zueon.springbootapp.tag.domain.entity.Tag;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
    private Profile profile = new Profile();

    @Embedded
    private NotificationSetting notificationSetting = new NotificationSetting();

    @PostLoad
    private void init(){
        if (profile == null) {
            profile = new Profile();
        }

        if (notificationSetting == null) {
            notificationSetting = new NotificationSetting();
        }

    }



    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    @Getter
    @ToString
    public static class Profile {
        private String bio;
        private String url;
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
        private boolean studyCreatedByEmail = false;
        private boolean studyCreatedByWeb = true;
        private boolean studyRegistrationResultByEmailByEmail = false;
        private boolean studyRegistrationResultByEmailByWeb = true;
        private boolean studyUpdatedByEmail = false;
        private boolean studyUpdatedByWeb = true;
    }

    private LocalDateTime joinedAt;

    private LocalDateTime emailTokenGeneratedAt;

    @ManyToMany
    @ToString.Exclude
    private Set<Tag> tags = new HashSet<>();

    public static Account with(String email, String nickname, String password){
        Account account = new Account();
        account.email = email;
        account.nickname = nickname;
        account.password = password;

        return  account;
    }

    public void generateToken(){
        this.emailToken = UUID.randomUUID().toString();
        this.emailTokenGeneratedAt = LocalDateTime.now();
    }

    public void verified(){
        this.isValid = true;
        joinedAt = LocalDateTime.now();

    }

    public boolean enableToSendEmail(){
        return this.emailTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(5));

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || Hibernate.getClass(this) != Hibernate.getClass(obj)) return false;

        Account account = (Account) obj;

        return id != null && Objects.equals(id, account.id);
    }

    public void updateProfile(com.zueon.springbootapp.settings.controller.Profile profile){
        if (this.profile == null) {
            this.profile = new Profile();
        }

        this.profile.bio = profile.getBio();
        this.profile.url = profile.getUrl();
        this.profile.job = profile.getJob();
        this.profile.location = profile.getLocation();
        this.profile.image = profile.getImage();

    }

    public void updatePassword(String newPassword){
        this.password = newPassword;

    }

    public void updateNotification(NotificationForm notificationForm) {
        this.notificationSetting.studyCreatedByEmail = notificationForm.isStudyCreatedByEmail();
        this.notificationSetting.studyCreatedByWeb = notificationForm.isStudyCreatedByWeb();
        this.notificationSetting.studyUpdatedByWeb = notificationForm.isStudyUpdatedByWeb();
        this.notificationSetting.studyUpdatedByEmail = notificationForm.isStudyUpdatedByEmail();
        this.notificationSetting.studyRegistrationResultByEmailByEmail = notificationForm.isStudyRegistrationResultByEmail();
        this.notificationSetting.studyRegistrationResultByEmailByWeb = notificationForm.isStudyRegistrationResultByWeb();
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;

    }

    public boolean isValid(String token){
        return this.emailToken.equals(token);
    }

}
