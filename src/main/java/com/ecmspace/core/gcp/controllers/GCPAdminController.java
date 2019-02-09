package com.ecmspace.core.gcp.controllers;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ecmspace.core.gcp.services.GCPAdminService;
import com.ecmspace.core.gcp.validators.GCPRequestValidator;
import com.google.api.services.storage.model.Bucket;


@RestController
@ComponentScan("com.ecmspace")
@RequestMapping("/gcp/admin")
public class GCPAdminController {
	
	@Autowired
	private GCPAdminService gcpAdminService;
	
	@Autowired
	private GCPRequestValidator gcpRequestValidator;
	
	@PostMapping("/create-bucket")
	public ResponseEntity<String> createBucket(@RequestBody Map<String, Object> params) {
		
		if(gcpRequestValidator.validateCreateBucketRequest(params) == false){
			return ResponseEntity.badRequest().body("Bucket name and Bucket location cannot be null.");
		}
		
		String bucketName = gcpAdminService.createBucket(params);
		URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/get-bucket/{bucketName}").buildAndExpand(bucketName).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PostMapping("/update-bucket")
	public ResponseEntity<String> updateBucket(@RequestBody Map<String, Object> params) {
		
		if(gcpRequestValidator.validateUpdateBucketRequest(params) == false){
			return ResponseEntity.badRequest().body("Bucket name cannot be null.");
		}
		
		String bucketName = gcpAdminService.updateBucket(params);
		URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/get-bucket/{bucketName}").buildAndExpand(bucketName).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@GetMapping("/delete-bucket/{bucketName}")
	public boolean deleteBucket(@PathVariable("bucketName") String bucketName) {
		return gcpAdminService.deleteBucket(bucketName);
	}
	
	@GetMapping("/get-bucket/{bucketName}")
	public Bucket getBucket(@PathVariable("bucketName") String bucketName) {
		return gcpAdminService.getBucket(bucketName);
	}
	

}
