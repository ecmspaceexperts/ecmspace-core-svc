package com.ecmspace.core.flowable;

import com.google.api.services.storage.model.StorageObject;

public class TaskObject {
	
	private String id;
	private String processInstanceId;
	private String name;
	private String attachmentId;
	private String attachementName;
	private String mediaLink;
	
	
	
	public String getMediaLink() {
		return mediaLink;
	}
	public void setMediaLink(String mediaLink) {
		this.mediaLink = mediaLink;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}
	public String getAttachementName() {
		return attachementName;
	}
	public void setAttachementName(String attachementName) {
		this.attachementName = attachementName;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	

}
