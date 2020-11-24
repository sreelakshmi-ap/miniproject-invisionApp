package com.miniproj.invision.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.model.Questionnaire;

@Service
public class UploadService {
	
	@Autowired
	QuestionnaireRepo qnrRepo;
	
	public String uploadFilesToQnr(MultipartFile sourcefile, File destFile, Integer q_id) throws IOException
	{
		
		destFile.createNewFile();
		FileOutputStream fout = new FileOutputStream(destFile);
		fout.write(sourcefile.getBytes());
		fout.close();
		
		return destFile.getAbsolutePath();
	}
	
	public String uploadFiles(MultipartFile sourcefile, File destFile) throws IOException
	{
	
		destFile.createNewFile();
		FileOutputStream fout = new FileOutputStream(destFile);
		fout.write(sourcefile.getBytes());
		fout.close();
		
		return destFile.getAbsolutePath();
	}
	

}
