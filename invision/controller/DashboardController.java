package com.miniproj.invision.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.dao.RolesRepo;
import com.miniproj.invision.model.ERoles;
import com.miniproj.invision.model.Employees;
import com.miniproj.invision.model.Questionnaire;
import com.miniproj.invision.model.Role;
import com.miniproj.invision.payload.response.MessageResponse;
import com.miniproj.invision.services.EmployeeService;
import com.miniproj.invision.services.MailService;
import com.miniproj.invision.services.QuestionnaireService;
import com.miniproj.invision.services.UploadService;

@RestController
@PreAuthorize("hasRole('ADMIN')or hasRole('SUPERADMIN')")
@RequestMapping("/questionnaire")
public class DashboardController {
	
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
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	EmployeeService userService;
	
	@Autowired
	UploadService uploadService;
	
	 Random random = new Random();
	 Set<Role> role;
	

	@PostMapping("/addQuestionnaire")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> addQuestionnaire( 
			 @RequestParam("questionnaire") String quest,
	         @RequestParam("pfile") MultipartFile file, 
	         @RequestBody List<Employees> list) throws IOException 
	 {
		
		Gson gson = new Gson(); Questionnaire qnr = gson.fromJson(quest, Questionnaire.class);
		File pptFile = new File("C:\\Users\\dell\\Documents\\workspace-spring\\invision\\PptFiles\\"+file.getOriginalFilename());
		String ppt_path = uploadService.uploadFiles(file, pptFile);
		qnr.setPpt_path(ppt_path);
		userService.addUsersList(list);
		
		qnrService.save(qnr);
		
		return ResponseEntity.ok(new MessageResponse("New questionnaire added successfully"));
	} 
	
	@GetMapping("/getQuestionnaire/{q_id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Optional<Questionnaire> getQuestionnaire(@PathVariable Integer q_id)
	{
		return qnrRepo.findById(q_id);
	}
	
	@Transactional
	@PostMapping("/publish/{q_id}")
	@ResponseStatus(HttpStatus.CREATED)
	public void publish(@PathVariable Integer q_id,  @RequestBody List<Employees> empList)
	 {
		Questionnaire qnr = qnrRepo.findById(q_id).get();
		String subject = "Regarding "+qnr.getTitle();
		
		for(int i = 0; i < empList.size(); i ++) {
			
			Employees emp = empList.get(i);
			
			String pwd = emp.generatePassword();
			String encodedPwd = encoder.encode(pwd);
			
			if(!userRepo.existsByEmail(emp.getEmail()))
			{
				role = new HashSet<>();
				Role userRole = roleRepo.findByName(ERoles.ROLE_USER).get();
				role.add(userRole);
				emp.setRoles(role);
				emp.setPassword(encodedPwd);
				emp.setImage_path("/images/"+emp.getUsername());
				userRepo.save(emp);
			}
			else
			{
				Employees previouslyExistingEmp = userRepo.findByUsername(emp.getUsername()).get();
				previouslyExistingEmp.setPassword(encodedPwd);
				userRepo.save(previouslyExistingEmp);
			}
			
			String mailBody = qnr.getMail_body()+" Login credentials"
					+ " Username:"+emp.getUsername()+" password:"+pwd+
					" NOTE: These will be your login credentials for company related other logins as well"+
					" click on the link to login  : https://127.0.0.1:8080/authenticate/login";
			String toUser = emp.getEmail();
			
			mailService.sendEmail(toUser, subject, mailBody);
			
			String sql = "insert into invision.status values('"+emp.getEmp_num()+"',"+q_id+",0,NULL)";
			Query query = entityManager.createNativeQuery(sql);
			query.executeUpdate();
		}
	 }
	
	
}
