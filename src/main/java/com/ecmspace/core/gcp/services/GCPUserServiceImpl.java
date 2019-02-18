package com.ecmspace.core.gcp.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecmspace.core.gcp.repositories.GCPRepository;
import com.google.api.gax.paging.Page;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.Blob;

@Service
@Qualifier("gcpUserService")
public class GCPUserServiceImpl implements GCPUserService{
	
	@Autowired
	private GCPRepository gcpRepository;

	@Override
	public String createBlob(Map<String, Object> params) {
		return gcpRepository.createBlob(params);
	}

	@Override
	public String updateBlob(Map<String, Object> params) {
		return gcpRepository.updateBlob(params);
	}

	@Override
	public boolean deleteBucket(String bucketName, String blobName) {
		return gcpRepository.deleteBlob(bucketName, blobName);
	}

	@Override
	public List<StorageObject> listAllBlobs(String bucketName, String folderPath) {
		return gcpRepository.listAllBlobs(bucketName,folderPath);
	}

	@Override
	public StorageObject getBlob(String bucketName, String blobName) {
		return gcpRepository.getBlob(bucketName, blobName);
	}
	
	@Override
	public String createFolder(Map<String, Object> params) {
		return gcpRepository.createFolder(params);
	}

	@Override
	public List<String> listAllFolders(String bucketName, String folderPath) {
		return gcpRepository.listAllFolders(bucketName, folderPath); 
	}

	@Override
	public StorageObject getFolder(String bucketName, String folderName) {
		return gcpRepository.getFolder(bucketName, folderName);
	}
	

}
