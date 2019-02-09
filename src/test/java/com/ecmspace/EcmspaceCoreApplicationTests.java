package com.ecmspace;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.ecmspace.core.config.Constants;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EcmspaceCoreApplicationTests {
	
	@Autowired
	TestRestTemplate testRestTemplate;


	@Test
	public void createBlobTest() {
		System.out.println("createblob service test..");
		
		JSONObject obj = new JSONObject();
		try {
			
			obj.put(Constants.GCP_KEY_BUCKET_NAME, "ecmspace-bucket-2");
			obj.put(Constants.GCP_KEY_BLOBNAME, "ecmspace-blob-test-01");
			obj.put(Constants.GCP_KEY_CONTENT, "This is my first blob using ecmspace api".getBytes(UTF_8));
			Map<String,String> metadata = new HashMap<>();
			metadata.put("division", "Finance");
			metadata.put("bunit", "ISI");
			metadata.put("sub-division", "Staff");
			obj.put(Constants.GCP_KEY_METADATA, metadata);
			
			ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/gcp/user/create-blob", obj.toString(), String.class);
			assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
			System.out.println(responseEntity.getHeaders().getLocation());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
    }

}

