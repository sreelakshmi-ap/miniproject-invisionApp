package com.miniproj.invision.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.miniproj.invision.dao.QuestionnaireRepo;
import com.miniproj.invision.model.FilePathGetter;

@Service
public class UploadService {
	
	@Autowired
	QuestionnaireRepo qnrRepo;
	
	private final Path fileStorageLocation;

	@Autowired
    public UploadService(FilePathGetter filePathGetter) throws IOException {
        this.fileStorageLocation = Paths.get(filePathGetter.getUploadDir())
                .toAbsolutePath().normalize();
            Files.createDirectories(this.fileStorageLocation);  
    }
	
	
	 public String storeFile(MultipartFile file) {
	        
	        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

	        try {
	      
	            if(fileName.contains("..")) {
	            	System.out.println("Unable to create the directory where the uploaded files will be stored");
	            }

	            Path targetLocation = this.fileStorageLocation.resolve(fileName);
	            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
	            return fileName;
	        } catch (IOException ex) {
	        	System.out.println("Unable to upload file\n Reason:"+ex.getMessage());
	        }
			return fileName;
	    }
	 
	 public Resource loadFileAsResource(String fileName) throws FileNotFoundException {
	        try {
	            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
	            Resource resource = new UrlResource(filePath.toUri());
	            if(resource.exists()) {
	                return resource;
	            } else {
	                throw new FileNotFoundException("File not found " + fileName);
	            }
	        } catch (MalformedURLException ex) {
	            throw new FileNotFoundException("File not found " + fileName);
	        }
	    }


}
