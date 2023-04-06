package com.zueon.springbootapp.study.application;

import com.zueon.springbootapp.account.domain.entity.Account;
import com.zueon.springbootapp.study.domain.entity.Study;
import com.zueon.springbootapp.study.endpoint.form.StudyDescriptionForm;
import com.zueon.springbootapp.study.endpoint.form.StudyForm;
import com.zueon.springbootapp.study.infra.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {
    private final StudyRepository studyRepository;

    public Study createNewStudy(StudyForm studyForm, Account account) {
        Study study = Study.toEntity(studyForm);
        study.addManager(account);

        return studyRepository.save(study);
    }

    public Study getStudy(Account account, String path) {
        Study study = studyRepository.findByPath(path);

        if (!account.isManagerOf(study)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }

        return study;
    }

    private Study getStudy(String path) {
        Study study = studyRepository.findByPath(path);
        if (study == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 존재하지 않습니다.");
        }
        return study;
    }

    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm){
        study.updateDescription(studyDescriptionForm);
    }

}
