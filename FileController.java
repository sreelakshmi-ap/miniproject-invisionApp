package com.miniproj.invision.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.miniproj.invision.dao.EmployeeRepo;
import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.dao.RolesRepo;
import com.miniproj.invision.model.ERoles;
import com.miniproj.invision.model.Employees;
import com.miniproj.invision.model.Questionnaire;
import com.miniproj.invision.model.Role;
import com.miniproj.invision.services.EmployeeServices;
import com.miniproj.invision.services.UploadService;

@RestController
@RequestMapping("/invision/files")

//Controller in case wrong file is uploaded to the questionnaire

public class FileController {
	
	@Autowired
	QuestionnaireRepo qnrRepo;
	
	@Autowired
	EmployeeRepo userRepo;

	@Autowired
	RolesRepo roleRepo;
	
	@Autowired
	EmployeeServices userService;
	
	@Autowired
	UploadService uploadService;
	
	@Autowired
	PasswordEncoder encoder;
	
	Random random = new Random();
	
	@RequestMapping(value = "/uploadPpt/{q_id}", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')or hasRole('SUPERADMIN')")
	public String uploadPptFiles (@RequestParam("file") MultipartFile file, @PathVariable(value = "q_id") Integer q_id) throws IOException 
	{
		Questionnaire qnr = qnrRepo.findById(q_id).get();
		
		File pptFile = new File("C:\\Users\\dell\\Documents\\workspace-spring\\invision\\PptFiles\\"+file.getOriginalFilename());
		
		qnr.setPpt_path(uploadService.uploadFilesToQnr(file, pptFile,q_id));
		qnrRepo.save(qnr);
		
		return "Success";
	}
	
	@RequestMapping(value = "/uploadXl", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ADMIN')or hasRole('SUPERADMIN')")
	public String uploadXlFile (@RequestParam("file") MultipartFile file) throws IOException 
	{
		File xlFile = new File("C:\\Users\\dell\\Documents\\workspace-spring\\invision\\XlFiles\\"+file.getOriginalFilename());
		return uploadService.uploadFiles(file, xlFile);
	}
	
}
