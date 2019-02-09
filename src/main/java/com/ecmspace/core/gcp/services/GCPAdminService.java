package com.ecmspace.core.gcp.services;

import java.util.Map;
import com.google.api.services.storage.model.Bucket;


public interface GCPAdminService {

	String createBucket(Map<String, Object> params);

	String updateBucket(Map<String, Object> params);

	boolean deleteBucket(String bucketName);

	Bucket getBucket(String bucketName);

}
