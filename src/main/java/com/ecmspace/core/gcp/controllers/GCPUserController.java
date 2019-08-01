package com.ecmspace.core.gcp.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ecmspace.core.config.Constants;
import com.ecmspace.core.flowable.FlowableService;
import com.ecmspace.core.flowable.TaskObject;
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
	private FlowableService flowableService;

	@Autowired
	private GCPRequestValidator gcpRequestValidator;

	@PostMapping("/create-blob")
	public ResponseEntity<String> createBlob(@RequestBody Map<String, Object> params) {

		if(gcpRequestValidator.validateCreateBlobRequest(params) == false){
			return ResponseEntity.badRequest().body("Invalid request parameters. Bucket name and Blob name are mandatory parameters.");
		}

		String blobName = gcpUserService.createBlob(params);
		String bucketName = (String) params.get(Constants.GCP_KEY_BUCKET_NAME);

		URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/get-blob")
				.queryParam("bucketName", bucketName)
				.queryParam("blobName", blobName)
				.buildAndExpand().toUri();

		return ResponseEntity.created(location).build();
	}

	@PostMapping("/create-folder")
	public ResponseEntity<String> createFolder(@RequestBody Map<String, Object> params) {

		if(gcpRequestValidator.validateCreateBlobRequest(params) == false){
			return ResponseEntity.badRequest().body("Invalid request parameters. Bucket name and Blob name are mandatory parameters.");
		}

		String blobName = gcpUserService.createFolder(params);
		String bucketName = (String) params.get(Constants.GCP_KEY_BUCKET_NAME);

		URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/get-folder")
				.queryParam("bucketName", bucketName)
				.queryParam("folderName", blobName)
				.buildAndExpand().toUri();
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

	@GetMapping("/delete-blob")
	public boolean deleteBlob(@RequestParam("bucketName") String bucketName, @RequestParam("blobName") String blobName) {
		return gcpUserService.deleteBucket(bucketName, blobName);
	}

	@GetMapping("/all-blobs")
	public List<StorageObject> listAllBlobs(@RequestParam("bucketname") String bucketName, @RequestParam("folderPath") String folderPath){
		return gcpUserService.listAllBlobs(bucketName,folderPath);
	}

	@GetMapping("/all-folders")
	public List<String> listAllFolders(@RequestParam("bucketname") String bucketName, @RequestParam("path") String folderPath){
		return gcpUserService.listAllFolders(bucketName,folderPath);
	}

	@GetMapping("/get-blob")
	public StorageObject getBlob(@RequestParam("bucketName") String bucketName, @RequestParam("blobName") String blobName) {
		return gcpUserService.getBlob(bucketName,blobName);
	}

	@GetMapping("/get-folder")
	public StorageObject getFolder(@RequestParam("bucketName") String bucketName, @RequestParam("folderName") String folderName) {
		return gcpUserService.getFolder(bucketName, folderName);
	}
	
	@GetMapping("/approve-document")
	public boolean approveDocument(@RequestParam("taskId") String taskId, @RequestParam("comment") String comment) {
		flowableService.approve(taskId,comment);
		return true;
	}
	
	@GetMapping("/reject-document")
	public boolean rejectDocument(@RequestParam("taskId") String taskId, @RequestParam("comment") String comment) {
		flowableService.reject(taskId,comment);
		return false;
	}
	
	@GetMapping("/claim-task")
	public boolean claimTask(@RequestParam("taskId") String taskId) {
		flowableService.claimTask(taskId);
		return true;
	}
	
	@GetMapping("/all-tasks")
	public List<TaskObject> claimTask() {
		return flowableService.getPendingTasks();
	}
	

}
