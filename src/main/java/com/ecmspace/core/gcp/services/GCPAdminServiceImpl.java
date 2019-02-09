package com.ecmspace.core.gcp.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ecmspace.core.gcp.repositories.GCPRepository;
import com.google.api.services.storage.model.Bucket;


@Service
@Qualifier("gcpAdminService")
public class GCPAdminServiceImpl implements GCPAdminService{
	
	@Autowired
	private GCPRepository gcpRepository;	
	
	@Override
	public String createBucket(Map<String, Object> params) {
		return gcpRepository.createBucket(params);
	}

	@Override
	public String updateBucket(Map<String, Object> params) {
		return gcpRepository.updateBucket(params);
	}

	@Override
	public boolean deleteBucket(String bucketName) {
		return gcpRepository.deleteBucket(bucketName);
	}

	@Override
	public Bucket getBucket(String bucketName) {
		return gcpRepository.getBucket(bucketName);
	}

}
