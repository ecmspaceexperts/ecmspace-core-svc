package com.ecmspace.core.gcp.validators;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ecmspace.core.config.Constants;

@Component
@Qualifier("gcpRequestValidator")
public class GCPRequestValidator {

	public boolean validateCreateBucketRequest(Map<String, Object> param){		
		if(isValid(param.get(Constants.GCP_KEY_BUCKET_NAME)) == false)
			return false;
		if(isValid(param.get(Constants.GCP_KEY_BUCKET_LOCATION)) == false)
			return false;
		return true;
	}
	
	public boolean validateUpdateBucketRequest(Map<String, Object> param) {
		if(isValid(param.get(Constants.GCP_KEY_BUCKET_NAME)) == false)
			return false;
		return true;
	}


	public boolean validateCreateBlobRequest(Map<String, Object> param) {
		if(isValid(param.get(Constants.GCP_KEY_BUCKET_NAME)) ==  false)
			return false;
		if(isValid(param.get(Constants.GCP_KEY_BLOBNAME)) == false)
			return false;
		return true;
	}

	public boolean validateUpdateBlobRequest(Map<String, Object> param) {
		if(isValid(param.get(Constants.GCP_KEY_BUCKET_NAME)) ==  false)
			return false;
		if(isValid(param.get(Constants.GCP_KEY_BLOBNAME)) == false)
			return false;
		return true;
	}

	
	private boolean isValid(Object value) {
		String str = (String) value;
		if(str != null && str.length() > 0)
			return true;
		return false;
	}
	
	
}
