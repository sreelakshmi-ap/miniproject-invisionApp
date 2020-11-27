package com.miniproj.invision.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.dao.RolesRepo;
import com.miniproj.invision.model.ERoles;
import com.miniproj.invision.model.Employees;
import com.miniproj.invision.model.Role;
import com.miniproj.invision.payload.response.MessageResponse;
import com.miniproj.invision.payload.response.PasswordResponse;

@Service
public class EmployeeService {

	@Autowired
	EmployeeRepo userRepo;

	@Autowired
	RolesRepo roleRepo;
	
	@Autowired
	PasswordEncoder encoder;
	
	Set<Role> role;
	
	Random random = new Random();

	public Employees save(Employees emp)
	{
		return userRepo.save(emp);
	}
	
	public Employees findByEmpNum(String emp_num)
	{
		return userRepo.findById(emp_num).get();
	}
	
	public HashMap<String, String> addEmployeesFromXl(MultipartFile excelDataFile) throws IOException
	{
		HashMap<String, String> userAndPwd = new HashMap<String, String>();
		
	    @SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook(excelDataFile.getInputStream());
	    XSSFSheet worksheet = workbook.getSheetAt(0);
	    
	    for(int i = 1; i< worksheet.getPhysicalNumberOfRows(); i++) {
	    	XSSFRow row = worksheet.getRow(i);
	    	
	    	if(!userRepo.existsById(row.getCell(0).getStringCellValue()))
		{
	        Employees user = new Employees(row.getCell(0).getStringCellValue(),
	        		row.getCell(1).getStringCellValue(), 
	        		row.getCell(2).getStringCellValue());
	    	Set<Role> role; role = new HashSet<>();
			Role userRole = roleRepo.findByName(ERoles.ROLE_USER).get();
			role.add(userRole);
			user.setRoles(role);
		
			String pwd = user.getUsername()+random.nextInt(10000);
			String encodedPwd = encoder.encode(pwd);
			user.setPassword(encodedPwd);
			user.setImage_path("/images/"+user.getUsername());
			
			userRepo.save(user);
			
			userAndPwd.put(user.getUsername(), pwd);
	    	}
	
	    }
	    return userAndPwd; 
	}
	
	public ResponseEntity<?> addAdmins(Employees emp)
	{
		if(!userRepo.existsById(emp.getEmp_num()))
		{
		role = new HashSet<>();
		Role adminRole = roleRepo.findByName(ERoles.ROLE_ADMIN).get();
				
		role.add(adminRole);
		emp.setRoles(role);

		String pwd = emp.getUsername()+random.nextInt(10000);
		String enc = encoder.encode(pwd);
		emp.setPassword(enc);
		emp.setImage_path("/images/"+emp.getUsername());
        userRepo.save(emp);
        
        return ResponseEntity.ok(new PasswordResponse(pwd)); 
	}
		else {
			return ResponseEntity.ok(new MessageResponse("Admin already exists"));
		}
		
	}
	
	public ResponseEntity<?> addSuperAdmins(Employees emp)
	{
		if(!userRepo.existsById(emp.getEmp_num()))
		{
		role = new HashSet<>();
		Role adminRole = roleRepo.findByName(ERoles.ROLE_SUPERADMIN).get();
				
		role.add(adminRole);
		emp.setRoles(role);

		String pwd = emp.getUsername()+random.nextInt(10000);
		String enc = encoder.encode(pwd);
		emp.setPassword(enc);
		emp.setImage_path("/images/"+emp.getUsername());
        userRepo.save(emp);
        
        return ResponseEntity.ok(new PasswordResponse(pwd)); 
	}
		else {
			return ResponseEntity.ok(new MessageResponse("Super Admin already exists"));
		}
		
	}
	public void addUsersList(List<Employees> list)
	{
		
		for(int i = 0; i < list.size(); i ++) {
			Employees emp = list.get(i);
			if(!userRepo.existsById(emp.getEmp_num()))
			{
			role = new HashSet<>();
			Role userRole = roleRepo.findByName(ERoles.ROLE_USER).get();
			role.add(userRole);
			emp.setRoles(role);
		
			String pwd = emp.getUsername()+random.nextInt(10000);
			String encodedPwd = encoder.encode(pwd);
			emp.setPassword(encodedPwd);
			emp.setImage_path("/images/"+emp.getUsername());
			userRepo.save(emp);
       
			//userAndPwd.put(emp.getUsername(), pwd);
			}
			
		}
		
	}
}

