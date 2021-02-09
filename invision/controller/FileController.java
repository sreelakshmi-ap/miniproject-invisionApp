package com.miniproj.invision.controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.dao.RolesRepo;

import com.miniproj.invision.model.FilePathGetter;
import com.miniproj.invision.model.Questionnaire;
import com.miniproj.invision.services.EmployeeService;
import com.miniproj.invision.services.UploadService;

@RestController
@PreAuthorize("hasRole('ADMIN')or hasRole('SUPERADMIN')")
@RequestMapping("/files")

public class FileController {
	
	@Autowired
	QuestionnaireRepo qnrRepo;
	
	@Autowired
	EmployeeRepo userRepo;

	@Autowired
	RolesRepo roleRepo;
	
	@Autowired
	EmployeeService userService;
	
	@Autowired
	UploadService uploadService;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	private FilePathGetter pathGetter;
	
	Random random = new Random();
	
	 @PutMapping("/uploadPptFile/{q_id}")
	    public HashMap<String, String> uploadPptToQuestionnaire(@RequestParam("file") MultipartFile file, @PathVariable(value = "q_id") Integer q_id) throws IOException {
	       
		 	HashMap<String, String> pptMap = new HashMap<>();
		 	String filename = uploadService.storeFile(file);
	        Questionnaire qnr = qnrRepo.findById(q_id).get();
	        qnr.setPpt_path(pathGetter.getUploadDir()+"/"+filename);
	        
	      pptMap.put("message", "PPT ("+filename+") uploaded to questionnaire successfully");
	      
	      return pptMap;
	 }

	 
	/* @GetMapping("/logout")
     public HashMap<String, String> fetchSignoutSite(HttpServletRequest request, HttpServletResponse response) {        
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         if (auth != null) {
             new SecurityContextLogoutHandler().logout(request, response, auth);
         }
         HashMap<String, String> mapResponse = new HashMap<>();
         mapResponse.put("message", "You are now logged out");
         return mapResponse;
	 }*/
	
}
