package com.ecmspace;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.flowable.engine.TaskService;
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
import com.ecmspace.core.flowable.FlowableService;
import com.google.api.services.storage.model.StorageObject;

import lombok.extern.log4j.Log4j2;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
public class EcmspaceCoreApplicationTests {
	
	//@Autowired
	//TestRestTemplate testRestTemplate;
	
	@Autowired
	private FlowableService flowableService;
	
	@Autowired
	private TaskService taskService;


	@Test
	public void contextLoads() {
		String filePath = "D:/GCP/Document01.docx";
		String contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		StorageObject obj = new StorageObject();
		Map<String, Object> variables = new HashMap<String, Object>();
		//variables.put(Constants.GCP_OBJECT_KEY, obj);
		String processInstanceId = flowableService.beginDocumentUploadProcess(filePath, contentType, variables);
		String tId = this.taskService.createTaskQuery().processInstanceId(processInstanceId).taskName("reviewDocument").singleResult().getId();
		log.info("started process..");
		
		
		
		Map<String,Object> map = flowableService.getProcessVariables(processInstanceId);
		log.info("Variable size: "+map.size());
		Iterator itr = map.keySet().iterator();
		while(itr.hasNext()){
			String key = (String) itr.next();
			log.info("Key: "+key+" Value: "+map.get(key));
		}
		
		
		//String tIdProcess = (String) this.taskService.createTaskQuery().taskId(tId).singleResult().getProcessVariables().get(Constants.FLOWABLE_TASK_ID);
		//log.info("tIdProcess: "+tIdProcess);
		
		flowableService.approve(tId);
		log.info("approved");
	}
	
	/*
	
	//@Test
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
    
    */

}

