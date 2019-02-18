package com.ecmspace.core.gcp.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.google.api.gax.paging.Page;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.Blob;

public interface GCPUserService {

	String createBlob(Map<String, Object> params);
	
	String createFolder(Map<String, Object> params);

	String updateBlob(Map<String, Object> params);

	boolean deleteBucket(String bucketName, String blobName);

	List<StorageObject> listAllBlobs(String bucketName, String folderPath);
	
	List<String> listAllFolders(String bucketName, String folderPath);

	StorageObject getBlob(String bucketName, String blobName);
	
	StorageObject getFolder(String bucketName, String folderName);

}
