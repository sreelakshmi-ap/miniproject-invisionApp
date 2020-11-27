package com.miniproj.invision.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.dao.RolesRepo;
import com.miniproj.invision.model.Employees;
import com.miniproj.invision.model.Questionnaire;
import com.miniproj.invision.model.Role;
import com.miniproj.invision.payload.response.MessageResponse;
import com.miniproj.invision.payload.response.ReportResponse;
import com.miniproj.invision.services.EmployeeService;
import com.miniproj.invision.services.MailService;
import com.miniproj.invision.services.MapperService;
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
	MailService mailService;
	
	@Autowired
	EmployeeService userService;
	
	@Autowired
	UploadService uploadService;
	
	@Autowired
	MapperService mapperService;
	
	 Random random = new Random();
	 Set<Role> role;

	@PostMapping("/addQuestionnaire")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> addQuestionnaire( 
			 @RequestParam("questionnaire") String quest,
	         @RequestParam("pptfile") MultipartFile file, 
	         @RequestParam("xlfile") MultipartFile xlFile) throws IOException, JsonMappingException 
	 {

		Gson gson = new Gson(); Questionnaire qnr = gson.fromJson(quest, Questionnaire.class);
		File pptFile = new File("C:\\Users\\dell\\Documents\\workspace-spring\\invision\\PptFiles\\"+file.getOriginalFilename());
		String ppt_path = uploadService.uploadFiles(file, pptFile);
		qnr.setPpt_path(ppt_path);
		userService.addEmployeesFromXl(xlFile);

		qnrRepo.save(qnr);

		return ResponseEntity.ok(new MessageResponse("New questionnaire added successfully"));
	} 
	
	@GetMapping("/getQuestionnaire/{q_id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Optional<Questionnaire> getQuestionnaire(@PathVariable Integer q_id)
	{
		return qnrRepo.findById(q_id);
	}
	
	@PostMapping("/save/{q_id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> save(@PathVariable Integer q_id, 
		 @RequestParam("questionnaire") String quest,
         @RequestParam("pptfile") MultipartFile file, 
         @RequestParam("xlfile") MultipartFile xlFile) throws IOException  
 {

	Gson gson = new Gson(); Questionnaire qnr = gson.fromJson(quest, Questionnaire.class);
	
	File pptFile = new File("C:\\Users\\dell\\Documents\\workspace-spring\\invision\\PptFiles\\"+file.getOriginalFilename());
	String ppt_path = uploadService.uploadFiles(file, pptFile);
	qnr.setPpt_path(ppt_path);
	userService.addEmployeesFromXl(xlFile);

	qnrRepo.save(qnr);

		return ResponseEntity.ok(new MessageResponse("Saved successfully.!"));
	 }

	
	@Transactional
	@PostMapping("/publish/{q_id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> publish(@PathVariable Integer q_id,  @RequestBody List<Employees> empList) throws MailException, NoSuchElementException, MessagingException
	 {
		return mapperService.publishQuestionnaire(q_id, empList);
	 }
	
	@PostMapping("/remind/{q_id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> remind(@PathVariable Integer q_id,  @RequestBody List<Employees> empList) throws MailException, NoSuchElementException, MessagingException
	 {
		return mapperService.remindUser(q_id, empList);
	 }
	
	@GetMapping("/generateReport/{q_id}")
	public List<ReportResponse> generateReportFromList(@PathVariable Integer q_id)
	{
		return mapperService.generateReport(q_id);
	}
	
	
}
