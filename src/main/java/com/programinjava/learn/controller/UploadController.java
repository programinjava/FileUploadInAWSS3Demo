package com.programinjava.learn.controller;

import java.io.InputStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Controller
public class UploadController {
	
	@PostMapping("/upload")
	public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {


		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return "redirect:uploadStatus";
		}
//		bucket name 
		String bucketName ="atserve-photos";
//		get it from user or change it 
		String nameOffile ="myPhoto";
//		getting aws access 
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_SOUTH_1)
                .withCredentials(new ProfileCredentialsProvider())
                .build();
		boolean isBucketExist =s3Client.doesBucketExist(bucketName);
		if(!isBucketExist) {
			s3Client.createBucket(bucketName);
		}
		try {
			InputStream is = file.getInputStream();
			s3Client.putObject(new PutObjectRequest(bucketName,nameOffile,is,new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead));
			redirectAttributes.addFlashAttribute("message", "SuccessFully Uploaded On AWS S3");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "redirect:/uploadStatus";
	}
	
	@GetMapping("/uploadStatus")
	public String uploadStatus(ModelMap m) {
		return "Homepage";
	}
	
	@GetMapping("/upload")  
	public String displayHomePageForAlarm() {
		return "Homepage";
	}

}
