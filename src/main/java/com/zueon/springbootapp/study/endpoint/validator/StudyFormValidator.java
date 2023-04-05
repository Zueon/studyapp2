package com.zueon.springbootapp.study.endpoint.validator;

import com.zueon.springbootapp.study.endpoint.form.StudyForm;
import com.zueon.springbootapp.study.infra.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator {
    private final StudyRepository studyRepository;
    @Override
    public boolean supports(Class<?> clazz) {
        return StudyForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyForm studyForm = (StudyForm) target;
        if (studyRepository.existsByPath(studyForm.getPath()))
            errors.rejectValue("path", "wrong.path", "이미 사용중인 스터디 경로입니다");
    }
}
