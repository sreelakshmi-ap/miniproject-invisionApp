package com.miniproj.invision.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.dao.MapperRepo;
import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.dao.RolesRepo;
import com.miniproj.invision.model.ERoles;
import com.miniproj.invision.model.Employees;
import com.miniproj.invision.model.Questionnaire;
import com.miniproj.invision.model.Role;
import com.miniproj.invision.model.User_Qnr_Mapper;
import com.miniproj.invision.payload.response.MessageResponse;
import com.miniproj.invision.payload.response.ReportResponse;

@Service
public class MapperService {
	
	@Autowired
	MapperRepo mapperRepo;
	
	@Autowired
	EmployeeRepo userRepo;
	
	@Autowired
	RolesRepo roleRepo;
	
	@Autowired
	QuestionnaireRepo qnrRepo;
	
	@Autowired
	QuestionnaireService qnrService;

	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	MailService mailService;
	
	public ResponseEntity<?> userAgreed(int q_id) throws NoSuchElementException
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUserName = authentication.getName();
	    Employees currentUser = userRepo.findByUsername(currentUserName).get();
	    
	    Questionnaire qnr = qnrRepo.findById(q_id).get();
	    
	    if(LocalDate.now().isBefore(qnr.getEnd_date()))
	    {
	   
	    User_Qnr_Mapper mapping = mapperRepo.findByEmp_numAndQ_id(currentUser.getEmp_num(), q_id).get();
	   
	    if(!mapping.isStatus())
	    {
	    mapping.setStatus(true);
	    mapping.setDate_accepted(LocalDate.now());
	    
	    mapperRepo.save(mapping);
	    }
	    else
	    	return ResponseEntity.ok(new MessageResponse("You have already agreed to this policy"));
	    }
	    
	    else {
	    	return ResponseEntity.ok(new MessageResponse("The policy end date has been expired.!"
	    			+ " You must have signed on or before :"+qnr.getEnd_date()));
	    }
	    return ResponseEntity.ok(new MessageResponse("Thank you for accepting.!"));
	}
	
	public HashMap<Integer, String> pendingQuestionnaire(Employees employee) throws NoSuchElementException
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
	
	public List<ReportResponse> generateReport(int q_id) throws NoSuchElementException, IndexOutOfBoundsException
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
	
	public ResponseEntity<?> publishQuestionnaire(Integer q_id, List<Employees> empList) throws NoSuchElementException, MailException, MessagingException
	{	
		try {
		Questionnaire qnr = qnrRepo.findById(q_id).get();
		if((LocalDate.now().isBefore(qnr.getEnd_date())))
		{
		String subject = "Regarding "+qnr.getTitle();
		
		for(Employees emp: empList) {
		
			String password = emp.generatePassword();
			String encodedPwd = encoder.encode(password);
			Set<Role> role;
			
			if(!userRepo.existsById(emp.getEmp_num()))
			{
				role = new HashSet<>();
				Role userRole = roleRepo.findByName(ERoles.ROLE_USER).get();
				role.add(userRole);
				emp.setRoles(role);
				emp.setPassword(encodedPwd);
				emp.setImage_path("/images/"+emp.getUsername()+".jpg");
				userRepo.save(emp);
				User_Qnr_Mapper newMap = new User_Qnr_Mapper(emp.getEmp_num(), q_id, Boolean.FALSE);
				mapperRepo.save(newMap);
			}
			else
			{
				Employees previouslyExistingEmp = userRepo.findById(emp.getEmp_num()).get();
				previouslyExistingEmp.setPassword(encodedPwd);
				userRepo.save(previouslyExistingEmp);
			}
			
			String mailBody = qnr.getMail_body()+" Login credentials"
					+ " Username:"+emp.getUsername()+" password:"+password+
					" NOTE: These will be your login credentials for company related other logins as well"+
					" click on the link to login  : https://127.0.0.1:8080/authenticate/login"
					+ " Please accept before "+qnr.getEnd_date();
			String toUser = emp.getEmail();
			
			mailService.sendEmail(toUser, subject, mailBody);
			
			User_Qnr_Mapper mapping = mapperRepo.findByEmp_numAndQ_id(emp.getEmp_num(), qnr.getQ_id()).get();
				
			mapping.setDate_mail_sent(LocalDate.now());
			mapperRepo.save(mapping);
					//new User_Qnr_Mapper(emp.getEmp_num(), q_id, Boolean.FALSE, LocalDate.now());
			//mapperRepo.save(mapping);
			}
		return ResponseEntity.ok(new MessageResponse("Published mail to the users successfully.!"));
		}
		else
			return ResponseEntity.ok(new MessageResponse("The questionnaire end date has been expired.!"));
		}catch(NoSuchElementException nse)
		{
			System.out.println(nse.getMessage());
		}
		return null;
		}
	
	public ResponseEntity<?> remindUser(Integer q_id, List<Employees> empList) throws NoSuchElementException, MailException, MessagingException
	{
		try {
		Questionnaire qnr = qnrRepo.findById(q_id).get();
		if((LocalDate.now().isBefore(qnr.getEnd_date())))
		{
		String subject = "Reminder "+qnr.getTitle();
		
		for(Employees emp: empList) {
			
			User_Qnr_Mapper mapping = mapperRepo.findByEmp_numAndQ_id(emp.getEmp_num(), q_id).get();
			if(!mapping.isStatus())
			{
			String password = emp.generatePassword();
			String encodedPwd = encoder.encode(password);
			Set<Role> role;
			
			if(!userRepo.existsById(emp.getEmp_num()))
			{
				role = new HashSet<>();
				Role userRole = roleRepo.findByName(ERoles.ROLE_USER).get();
				role.add(userRole);
				emp.setRoles(role);
				emp.setPassword(encodedPwd);
				emp.setImage_path("/images/"+emp.getUsername()+".jpg");
				userRepo.save(emp);
			}
			else
			{
				Employees previouslyExistingEmp = userRepo.findById(emp.getEmp_num()).get();
				previouslyExistingEmp.setPassword(encodedPwd);
				userRepo.save(previouslyExistingEmp);
			}
			
			String mailBody = qnr.getMail_body()+".   Login credentials"
					+ " Username :"+emp.getUsername()+"                 password:"+password+
					"              NOTE: These will be your login credentials for company related other logins as well.      "+
					" click on the link to login  : https://127.0.0.1:8080/authenticate/login"
					+ "             Accept before "+qnr.getEnd_date();
			String toUser = emp.getEmail();
			
			mailService.sendEmail(toUser, subject, mailBody);
			mapping.setDate_mail_sent(LocalDate.now());
			mapperRepo.save(mapping);
			
			}
			}
		return ResponseEntity.ok(new MessageResponse("Reminder sent thru mail to the users successfully.!"));
		}
		else 
			return ResponseEntity.ok(new MessageResponse("End date of this questionnaire has been expired"
					+ "it was "+qnr.getEnd_date()));
		}
		catch(NoSuchElementException nse)
		{
			System.out.println(nse.getMessage());
		}
		return null;
	}
	
	public void mapUsersAndQnr(Integer q_id, List<Employees> empList) throws NoSuchElementException, MailException, MessagingException
	{
		
	for(Employees emp: empList) {
	
		String password = emp.generatePassword();
		String encodedPwd = encoder.encode(password);
		Set<Role> role;
		
		if(!userRepo.existsById(emp.getEmp_num()))
		{
			role = new HashSet<>();
			Role userRole = roleRepo.findByName(ERoles.ROLE_USER).get();
			role.add(userRole);
			emp.setRoles(role);
			emp.setPassword(encodedPwd);
			emp.setImage_path("/images/"+emp.getUsername()+".jpg");
			userRepo.save(emp);
		}
		else
		{
			Employees previouslyExistingEmp = userRepo.findById(emp.getEmp_num()).get();
			previouslyExistingEmp.setPassword(encodedPwd);
			userRepo.save(previouslyExistingEmp);
		}
		User_Qnr_Mapper mapping = new User_Qnr_Mapper(emp.getEmp_num(), q_id, Boolean.FALSE);
		mapperRepo.save(mapping);
	}
	}
	
}
