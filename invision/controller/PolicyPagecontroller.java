package com.miniproj.invision.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.dao.RolesRepo;
import com.miniproj.invision.model.Employees;
import com.miniproj.invision.model.Questionnaire;
import com.miniproj.invision.payload.response.MessageResponse;
import com.miniproj.invision.services.EmployeeService;
import com.miniproj.invision.services.MailService;
import com.miniproj.invision.services.UploadService;

@RestController
public class PolicyPagecontroller 
{
	
	@Autowired
	EmployeeRepo userRepo;
	
	@Autowired
	QuestionnaireRepo qnrRepo;

	@Autowired
	RolesRepo roleRepo;
	
	@Autowired
	EmployeeService userService;
	
	@Autowired
	UploadService uploadService;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	MailService mailService;
	
	@PersistenceContext
	EntityManager em;

	@RequestMapping(value = "/newPassword", method = RequestMethod.PUT)
	public ResponseEntity<?> sendMailWithNewPassword (@RequestBody Employees emp) {

	    Employees employee = userRepo.findByUsername(emp.getUsername()).get();

	    String newPassword = employee.generatePassword();
	    String encodedPassword = encoder.encode(newPassword);
	    
	    employee.setPassword(encodedPassword);
	    
	    String toUser = employee.getEmail();
	    String subject = "Password Reset";
	    String body = "Your login password has been updated to "+newPassword+" "
	    		+ "NOTE: You should use THIS password for all the logins from now";
	    
	    mailService.sendEmail(toUser, subject, body);
	    
	    userRepo.save(employee);
	    
	    return ResponseEntity.ok(new MessageResponse("Check your mail "+toUser+" for updated password"));
	    
	}
	
	@GetMapping("/getPptSource/{q_id}")
	@PreAuthorize("hasRole('ADMIN')or hasRole('SUPERADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public String getPpt(@PathVariable Integer q_id)
	{
		Questionnaire qnr = qnrRepo.findById(q_id).get();
		return qnr.getPpt_path();
	}

}
