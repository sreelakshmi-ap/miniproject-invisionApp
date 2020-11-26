package com.miniproj.invision.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.dao.MapperRepo;
import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.model.Employees;
import com.miniproj.invision.model.Questionnaire;
import com.miniproj.invision.model.User_Qnr_Mapper;
import com.miniproj.invision.payload.response.ReportResponse;

@Service
public class MapperService {
	
	@Autowired
	MapperRepo mapperRepo;
	
	@Autowired
	EmployeeRepo userRepo;
	
	@Autowired
	QuestionnaireRepo qnrRepo;
	
	public void userAgreed(int q_id)
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUserName = authentication.getName();
	    Employees currentUser = userRepo.findByUsername(currentUserName).get();
	    
	    User_Qnr_Mapper mapping = mapperRepo.findByEmp_numAndQ_id(currentUser.getEmp_num(), q_id).get();
	    
	    mapping.setStatus(true);
	    mapping.setDate_accepted(LocalDate.now());
	    
	    mapperRepo.save(mapping);
	}
	
	public HashMap<Integer, String> pendingQuestionnaire(Employees employee)
	{
		HashMap<Integer, String> idAndTitle = new HashMap<>();
		String title;
		Questionnaire qnr;
		List<Integer> q_idList = mapperRepo.getPendingQuestionnaires(employee.getEmp_num());
		for(int q_id: q_idList)
		{
			qnr = qnrRepo.getOne(q_id);
			title = qnr.getTitle();
			
			idAndTitle.put(q_id, title);
		}
		return idAndTitle;
	}
	
	public HashMap<Integer, String> completedQuestionnaire(Employees employee)
	{
		HashMap<Integer, String> idAndTitle = new HashMap<>();
		String title;
		Questionnaire qnr;
		List<Integer> q_idList = mapperRepo.getCompletedQuestionnaires(employee.getEmp_num());
		for(int q_id: q_idList)
		{
			qnr = qnrRepo.getOne(q_id);
			title = qnr.getTitle();
			
			idAndTitle.put(q_id, title);
		}
		return idAndTitle;
	}
	
	public List<ReportResponse> generateReport(int q_id)
	{
	List<String> reportList = mapperRepo.getReport(q_id);
	String username, title, status, acceptedOn, mailSent;
	ReportResponse report;
	List<ReportResponse> reportResponseList = new ArrayList<>();
	
	for(String list: reportList)
	{
		String[] reportValues = list.split(",");
		title = reportValues[0];
		username = reportValues[1];
		status = reportValues[2].toString();
		acceptedOn = reportValues[3].toString();
		mailSent = reportValues[4].toString();
		
		report = new ReportResponse(username,title,status,mailSent,acceptedOn);
		
		reportResponseList.add(report);
	}
	return reportResponseList;
	
	}

}
