package com.miniproj.invision.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.miniproj.invision.model.Questionnaire;

public interface QuestionnaireRepo extends JpaRepository<Questionnaire, Integer> {

}
