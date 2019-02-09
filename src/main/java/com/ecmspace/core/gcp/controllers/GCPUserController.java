package com.ecmspace.core.gcp.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ecmspace.core.config.Constants;
import com.ecmspace.core.gcp.services.GCPUserService;
import com.ecmspace.core.gcp.validators.GCPRequestValidator;
import com.google.api.services.storage.model.StorageObject;

@RestController
@ComponentScan("com.ecmspace")
@RequestMapping("/gcp/user")
public class GCPUserController {
	
	@Autowired
	private GCPUserService gcpUserService;
	
	@Autowired
	private GCPRequestValidator gcpRequestValidator;
	
	@PostMapping("/create-blob")
	public ResponseEntity<String> createBlob(@RequestBody Map<String, Object> params) {
		
		if(gcpRequestValidator.validateCreateBlobRequest(params) == false){
			return ResponseEntity.badRequest().body("Invalid request parameters. Bucket name and Blob name are mandatory parameters.");
		}
		
		String blobName = gcpUserService.createBlob(params);
		String bucketName = (String) params.get(Constants.GCP_KEY_BUCKET_NAME);
		URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/get-blob/{bucketName}/{blobName}").buildAndExpand(bucketName,blobName).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PostMapping("/update-blob")
	public ResponseEntity<String> updateBlob(@RequestBody Map<String, Object> params) {
		
		if(gcpRequestValidator.validateUpdateBlobRequest(params) == false){
			return ResponseEntity.badRequest().body("Invalid request parameters. Bucket name and Blob name are mandatory parameters.");
		}
		
		String blobName = gcpUserService.createBlob(params);
		String bucketName = (String) params.get(Constants.GCP_KEY_BUCKET_NAME);
		URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/get-blob/{bucketName}/{blobName}").buildAndExpand(bucketName,blobName).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@GetMapping("/delete-blob/{bucketName}/{blobName}")
	public boolean deleteBlob(@PathVariable("bucketName") String bucketName, @PathVariable("blobName") String blobName) {
		return gcpUserService.deleteBucket(bucketName, blobName);
	}
	
	@GetMapping("/all-blobs")
	public List<StorageObject> listAllBlobs(@RequestParam("bucketname") String bucketName){
		return gcpUserService.listAllBlobs(bucketName);
	}
	
	@GetMapping("/get-blob/{bucketName}/{blobName}")
	public StorageObject getBlob(@PathVariable("bucketName") String bucketName, @PathVariable("blobName") String blobName) {
		return gcpUserService.getBlob(bucketName,blobName);
	}

}
