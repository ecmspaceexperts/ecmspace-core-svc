package com.ecmspace.core.gcp.config;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.ecmspace.core.aop.NoLogging;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;

import com.google.cloud.storage.StorageOptions;


@Configuration
@ComponentScan("com.ecmspace")
public class GCPConfiguration {

	@NoLogging
	@Bean
	public Storage storageInstace(){
		
		HttpTransport transport = null;
		GoogleCredential credential = null;
		JsonFactory jsonFactory = new JacksonFactory();
		try {
			transport = GoogleNetHttpTransport.newTrustedTransport();
			credential = GoogleCredential.getApplicationDefault(transport, jsonFactory);
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}
	
	    // Depending on the environment that provides the default credentials (for
	    // example: Compute Engine, App Engine), the credentials may require us to
	    // specify the scopes we need explicitly.  Check for this case, and inject
	    // the Cloud Storage scope if required.
	    if (credential.createScopedRequired()) {
	      Collection<String> scopes = StorageScopes.all();
	      credential = credential.createScoped(scopes);
	    }

	    return new Storage.Builder(transport, jsonFactory, credential)
	        .setApplicationName("ecmspace-core")
	        .build();
		
		//return StorageOptions.getDefaultInstance().getService();
	}
	
	
}
