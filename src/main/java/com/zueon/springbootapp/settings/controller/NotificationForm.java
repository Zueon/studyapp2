package com.zueon.springbootapp.settings.controller;

import com.zueon.springbootapp.account.domain.entity.Account;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationForm {
    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyRegistrationResultByEmail;
    private boolean studyRegistrationResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;

    protected NotificationForm(Account account) {
        this.studyCreatedByEmail = account.getNotificationSetting().isStudyCreatedByEmail();
        this.studyCreatedByWeb = account.getNotificationSetting().isStudyCreatedByWeb();
        this.studyUpdatedByEmail = account.getNotificationSetting().isStudyUpdatedByEmail();
        this.studyUpdatedByWeb = account.getNotificationSetting().isStudyUpdatedByWeb();
        this.studyRegistrationResultByEmail = account.getNotificationSetting().isStudyRegistrationResultByEmailByEmail();
        this.studyRegistrationResultByWeb = account.getNotificationSetting().isStudyRegistrationResultByEmailByWeb();
    }

    public static NotificationForm from(Account account) {
        return new NotificationForm(account);
    }
}
