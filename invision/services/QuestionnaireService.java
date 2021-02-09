package com.miniproj.invision.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.model.Questionnaire;

@Service
public class QuestionnaireService
{
	@Autowired
	QuestionnaireRepo qnrRepo;
	
	public void save(Questionnaire qnr)
	{
		 qnrRepo.save(qnr);
	}
}
