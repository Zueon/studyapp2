package com.zueon.springbootapp.study.application;

import com.zueon.springbootapp.account.domain.entity.Account;
import com.zueon.springbootapp.study.domain.entity.Study;
import com.zueon.springbootapp.study.endpoint.form.StudyForm;
import com.zueon.springbootapp.study.infra.StudyRepository;
import lombok.RequiredArgsConstructor;
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

}
