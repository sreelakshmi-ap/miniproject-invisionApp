package com.miniproj.invision.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.dao.RolesRepo;
import com.miniproj.invision.dao.StatusRepo;
import com.miniproj.invision.model.ERoles;
import com.miniproj.invision.model.Employees;
import com.miniproj.invision.model.Questionnaire;
import com.miniproj.invision.model.Role;
import com.miniproj.invision.model.Status;
import com.miniproj.invision.payload.response.AdminsResponse;
import com.miniproj.invision.payload.response.MessageResponse;
import com.miniproj.invision.payload.response.PasswordResponse;
import com.miniproj.invision.services.EmployeeServices;
import com.miniproj.invision.services.MailService;
import com.miniproj.invision.services.UploadService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/invision")
public class AdminsAndSAdminsController {


	@Autowired
	EmployeeRepo userRepo;
	
	@Autowired
	QuestionnaireRepo qnrRepo;

	@Autowired
	RolesRepo roleRepo;
	
	@Autowired
	StatusRepo statusRepo;
	
	@Autowired
	EmployeeServices userService;
	
	@Autowired
	UploadService uploadService;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	MailService mailService;
	
	 Random random = new Random();
	 Set<Role> role;

		@GetMapping("/getSuperAdmins")
		@PreAuthorize("hasRole('SUPERADMIN')")
		public List<AdminsResponse>  allSuperAdmins() {
		
			List<AdminsResponse> adminList = new ArrayList<>();
			AdminsResponse response;
			List<Employees> empl =  userRepo.findAllSuperAdmins();
			for(int i = 0; i < empl.size(); i ++)
			{
				response = new AdminsResponse(empl.get(i).getEmp_num(), empl.get(i).getUsername(), empl.get(i).getEmail());
			
				adminList.add(response);
			}
			return adminList;
		}
		
		@GetMapping("/getAdmins")
		@PreAuthorize("hasRole('SUPERADMIN')")
		public List<AdminsResponse>  allAdmins() {
		
			List<AdminsResponse> adminList = new ArrayList<>();
			AdminsResponse response;
			List<Employees> empl =  userRepo.findAllAdmins();
			for(int i = 0; i < empl.size(); i ++)
			{
				response = new AdminsResponse(empl.get(i).getEmp_num(), empl.get(i).getUsername(), empl.get(i).getEmail());
			
				adminList.add(response);
			}
			return adminList;
		
		}

		@PostMapping("/addAdmins")
		@PreAuthorize("hasRole('SUPERADMIN')")
		@ResponseStatus(HttpStatus.CREATED) 
	    public ResponseEntity<?> addAdmins( 
	        @RequestBody Employees emp) 
	    { 
			return userService.addAdmins(emp);
	        
	    } 
		
		@PostMapping("/addSuperAdmins")
		@PreAuthorize("hasRole('SUPERADMIN')")
		@ResponseStatus(HttpStatus.CREATED) 
	    public ResponseEntity<?> addSuperAdmins( 
	        @RequestBody Employees emp) 
	    { 
			return userService.addSuperAdmins(emp);
	    } 
		
		
		@PostMapping("/addUsersList")
		@PreAuthorize("hasRole('ADMIN')or hasRole('SUPERADMIN')")
		@ResponseStatus(HttpStatus.CREATED) 
	    public HashMap<String, String> addUsersList( 
	        @RequestBody List<Employees> employee) 
	    
	    { 
			HashMap<String, String> userAndPwd = new HashMap<String, String>();
			for(int i = 0; i < employee.size(); i ++) {
				Employees emp = employee.get(i);
				role = new HashSet<>();
				Role userRole = roleRepo.findByName(ERoles.ROLE_USER).get();
				role.add(userRole);
				emp.setRoles(role);
			
				String pwd = emp.getUsername()+random.nextInt(10000);
				String encodedPwd = encoder.encode(pwd);
				emp.setPassword(encodedPwd);
				emp.setImage_path("/images/"+emp.getUsername());
				userRepo.save(emp);
	       
				userAndPwd.put(emp.getUsername(), pwd);
			}
			return userAndPwd;
	    }
		
		@PutMapping("/update/{emp_num}")
		@PreAuthorize("hasRole('SUPERADMIN')")
		  @ResponseStatus(HttpStatus.OK) 
	    public ResponseEntity<?> updateEmployee(@RequestBody Employees emp,  
	    		 @PathVariable(value = "emp_num") String emp_num) throws NoSuchElementException
	    
	    { 
			Employees employee = userService.findByEmpNum(emp_num);
		
			employee.setEmail(emp.getEmail());
			employee.setUsername(emp.getUsername());

	        userRepo.save(employee);
	       
	        return ResponseEntity.ok(new MessageResponse("Updated Successfully.!")); 
	    } 

		@DeleteMapping("/remove/{emp_num}")
		@PreAuthorize("hasRole('SUPERADMIN')")
		public ResponseEntity<?> deleteAdminsAndSuperAdmins(  @PathVariable(value = "emp_num") String emp_num) throws NoSuchElementException
	      {
			userRepo.deleteById(emp_num);
			
			return ResponseEntity.ok(new MessageResponse("Deleted Successfully.!"));
		}
		
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
		
		@PostMapping("/addQuestionnaire")
		@PreAuthorize("hasRole('ADMIN')or hasRole('SUPERADMIN')")
		@ResponseStatus(HttpStatus.CREATED)
		public ResponseEntity<?> addQuestionnaire( 
				 @RequestParam("questionnaire") String quest,
		         @RequestParam("pfile") MultipartFile file, 
		         @RequestParam("xfile") MultipartFile xlFile) throws IOException 
		 {
			
			Gson gson = new Gson(); Questionnaire qnr = gson.fromJson(quest, Questionnaire.class);
			File pptFile = new File("C:\\Users\\dell\\Documents\\workspace-spring\\invision\\PptFiles\\"+file.getOriginalFilename());
			String ppt_path = uploadService.uploadFiles(file, pptFile);
			qnr.setPpt_path(ppt_path);
			userService.addEmployeesFromXl(xlFile);
			
			qnrRepo.save(qnr);
			
			return ResponseEntity.ok(new MessageResponse("New questionnaire added successfully"));
		} 


	}



