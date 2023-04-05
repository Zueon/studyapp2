package com.zueon.springbootapp.study.infra;

import com.zueon.springbootapp.study.domain.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);

    Study findByPath(String path);
}