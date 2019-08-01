package com.ecmspace.core.flowable;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.bcel.Const;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Attachment;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecmspace.core.config.Constants;
import com.google.api.services.storage.model.StorageObject;

@Service
public class FlowableService {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	private final Log logger = LogFactory.getLog(FlowableService.class);

	public String beginFlowableProcess(String processName, Map<String,Object> variables){		
		ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(processName,variables);
		return processInstance.getId();
	}

	public String beginDocumentUploadProcess(String filePath, String contentType, Map<String, Object> variables) {	
		try {
			logger.info("Starting process: "+Constants.DOCUMENT_UPLOAD_PROCESS_NAME);
			variables.put(Constants.FLOWABLE_TASK_ID, "51229111-7603-11e9-94c1-685d43f97f1c");
			logger.info("Variable size: "+variables.size());
			ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(Constants.DOCUMENT_UPLOAD_PROCESS_NAME,variables);
			String processInstanceId = processInstance.getId();
			logger.info("Started process: "+processInstanceId);
			File attachment = new File(filePath);
			logger.info("Attachement exists ? : "+attachment.exists());
			Task userReviewDocTask = this.taskService.createTaskQuery().processInstanceId(processInstanceId).taskName("reviewDocument").singleResult();
			String taskId = userReviewDocTask.getId();
			logger.info("UserReviewDocTask task id : "+taskId);
			variables.put(Constants.FLOWABLE_TASK_ID, taskId);
			runtimeService.setVariables(processInstanceId, variables);
			
			//runtimeService.setVariable(processInstanceId, Constants.FLOWABLE_TASK_ID, taskId);
			this.taskService.createAttachment(contentType, userReviewDocTask.getId(), processInstanceId, attachment.getName(), "Please review.", new FileInputStream(attachment));
			return processInstanceId;
		} catch(FlowableException fe){
			fe.printStackTrace();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	public List<TaskObject> getPendingTasks(){
		List<Task> tasks = this.taskService.createTaskQuery()
				.active()
				.taskName("reviewDocument")
				.includeProcessVariables()
				.list();

		List<TaskObject> taskList = new ArrayList<TaskObject>();

		for(Task t : tasks){
			TaskObject tobj = new TaskObject();
			tobj.setMediaLink((String)runtimeService.getVariables(t.getProcessInstanceId()).get(Constants.GCP_DOWNLOAD_MEDIA_LINK));
			tobj.setId(t.getId());
			tobj.setName(t.getName());
			tobj.setProcessInstanceId(t.getProcessInstanceId());

			Attachment attachment = this.taskService.getTaskAttachments(t.getId()).get(0);
			tobj.setAttachementName(attachment.getName());
			tobj.setAttachmentId(attachment.getId());
			taskList.add(tobj);

		}

		System.out.println("Total pending tasks : "+taskList.size());
		return taskList;

	}

	public void claimTask(String taskId){
		this.taskService.claim(taskId, "jlong");
	}

	public void approve(String taskId, String comment){
		try{
			this.taskService.setVariable(taskId, "canPromote", true);
			//this.taskService.setVariable(taskId, Constants.FLOWABLE_TASK_ID, taskId);
			Task t =  this.taskService.createTaskQuery().taskId(taskId).singleResult();
			this.taskService.addComment(taskId, t.getProcessInstanceId(), comment);
			this.taskService.complete(taskId);
		}catch(FlowableException e){
			e.printStackTrace();
		}
	}

	public void reject(String taskId, String comment){
		try{
			this.taskService.setVariable(taskId, "canPromote", false);
			Task t =  this.taskService.createTaskQuery().taskId(taskId).singleResult();
			this.taskService.addComment(taskId, t.getProcessInstanceId(), comment);
			this.taskService.complete(taskId);
		}catch(FlowableException e){
			e.printStackTrace();
		}
	}

	public Map<String, Object> getProcessVariables(String taskId) {
		try{
			String processInstanceId = this.taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
			return runtimeService.getVariables(processInstanceId);
			//
			//logger.info("From getProcessVariables: processId="+task.getProcessInstanceId());
			//logger.info("From getProcessVariables: variables size="+task.getProcessVariables().size());
			//return task.getProcessVariables();
		}catch(FlowableException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public Map<String, Object> getProcessVariablesByProcessId(String processInstanceId) {
		try{
			return runtimeService.getVariables(processInstanceId);
		}catch(FlowableException e){
			e.printStackTrace();
		}
		return null;
	}


}
