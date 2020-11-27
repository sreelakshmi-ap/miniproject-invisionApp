package com.miniproj.invision.controller;

import java.util.HashMap;
import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.dao.MapperRepo;
import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.model.Employees;
import com.miniproj.invision.payload.response.MessageResponse;
import com.miniproj.invision.services.EmployeeService;
import com.miniproj.invision.services.MailService;
import com.miniproj.invision.services.MapperService;
import com.miniproj.invision.services.UploadService;

@RestController
@RequestMapping("/policy")
public class PolicyPageController 
{
	
	@Autowired
	EmployeeRepo userRepo;
	
	@Autowired
	QuestionnaireRepo qnrRepo;
	
	@Autowired
	EmployeeService userService;
	
	@Autowired
	UploadService uploadService;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	MapperRepo mapperRepo;
	
	@Autowired
	MailService mailService;
	
	@Autowired
	MapperService mapperService;

	@RequestMapping(value = "/forgotPassword", method = RequestMethod.PUT)
	public ResponseEntity<?> sendMailWithNewPassword (@RequestBody Employees emp) throws MailException, MessagingException
	{
	    Employees employee = userRepo.findByUsername(emp.getUsername()).get();

	    String newPassword = employee.generatePassword();
	    String encodedPassword = encoder.encode(newPassword);
	    
	    employee.setPassword(encodedPassword);
	    
	    String toUser = employee.getEmail();
	    String subject = "Password Reset";
	    String body = "Your login password has been updated to "+newPassword+" "
	    		+ " NOTE: You should use THIS password for all the logins from now";
	    
	    mailService.sendEmail(toUser, subject, body);
	    
	    userRepo.save(employee);
	    
	    return ResponseEntity.ok(new MessageResponse("Check your mail "+toUser+" for updated password"));
	    
	}
	
	@GetMapping("/pending")
	public HashMap<Integer,String> getPending()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUserName = authentication.getName();
	    Employees currentUser = userRepo.findByUsername(currentUserName).get();
	    
	    return mapperService.pendingQuestionnaire(currentUser);
	}
	
	@GetMapping("/completed")
	public HashMap<Integer,String> getCompleted()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUserName = authentication.getName();
	    Employees currentUser = userRepo.findByUsername(currentUserName).get();
	    
	    return mapperService.completedQuestionnaire(currentUser);
	}
	
	@GetMapping("/getPptSource/{q_id}")
	public String getPpt(@PathVariable Integer q_id)
	{
		return qnrRepo.findById(q_id).get().getPpt_path();
	}
	
	@PutMapping("/agreed/{q_id}")
	public ResponseEntity<?> changeStatus(@PathVariable Integer q_id)
	{
		mapperService.userAgreed(q_id);
		return ResponseEntity.ok(new MessageResponse("Thank you for accepting the policy.!"));
	}
	
}
