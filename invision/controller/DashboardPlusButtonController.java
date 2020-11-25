package com.miniproj.invision.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.springframework.web.bind.annotation.RestController;

import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.dao.RolesRepo;
import com.miniproj.invision.model.Employees;
import com.miniproj.invision.model.Role;
import com.miniproj.invision.payload.response.Adminsview;
import com.miniproj.invision.payload.response.MessageResponse;
import com.miniproj.invision.services.EmployeeService;
import com.miniproj.invision.services.MailService;
import com.miniproj.invision.services.UploadService;

import java.util.ArrayList;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/invision")
@PreAuthorize("hasRole('SUPERADMIN')")
public class DashboardPlusButtonController {


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
	
	 Random random = new Random();
	 Set<Role> role;

		@GetMapping("/getSuperAdmins")
		public List<Adminsview>  allSuperAdmins() {
		
			List<Adminsview> adminList = new ArrayList<>();
			Adminsview response;
			List<Employees> empl =  userRepo.findAllSuperAdmins();
			for(int i = 0; i < empl.size(); i ++)
			{
				response = new Adminsview(empl.get(i).getEmp_num(), empl.get(i).getUsername(), empl.get(i).getEmail());
			
				adminList.add(response);
			}
			return adminList;
		}
		
		@GetMapping("/getAdmins")
		public List<Adminsview>  allAdmins() {
		
			List<Adminsview> adminList = new ArrayList<>();
			Adminsview response;
			List<Employees> admins =  userRepo.findAllAdmins();
			for(int i = 0; i < admins.size(); i ++)
			{
				response = new Adminsview(admins.get(i).getEmp_num(), admins.get(i).getUsername(), admins.get(i).getEmail());
			
				adminList.add(response);
			}
			return adminList;
		
		}

		@PostMapping("/addAdmins")
		@ResponseStatus(HttpStatus.CREATED) 
	    public ResponseEntity<?> addAdmins( 
	        @RequestBody Employees emp) 
	    { 
			return userService.addAdmins(emp);
	        
	    } 
		
		@PostMapping("/addSuperAdmins")
		@ResponseStatus(HttpStatus.CREATED) 
	    public ResponseEntity<?> addSuperAdmins( 
	        @RequestBody Employees emp) 
	    { 
			return userService.addSuperAdmins(emp);
	    } 
		
		@PutMapping("/update/{emp_num}")
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
		public ResponseEntity<?> deleteAdminsAndSuperAdmins(  @PathVariable(value = "emp_num") String emp_num) throws NoSuchElementException
	      {
			userRepo.deleteById(emp_num);
			
			return ResponseEntity.ok(new MessageResponse("Deleted Successfully.!"));
		}
		
		
		@RequestMapping(value = "/changePassword", method = RequestMethod.PUT)  
	    public ResponseEntity<?> currentUserNameSimple(@RequestBody Employees emp) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    String currentUserName = authentication.getName();
		  
		    Employees employee = userRepo.findByUsername(currentUserName).get();
		    String encodedPwd = encoder.encode(emp.getPassword());
		    employee.setPassword(encodedPwd);
		    
		    userRepo.save(employee);

		    return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));

		    }

	}



