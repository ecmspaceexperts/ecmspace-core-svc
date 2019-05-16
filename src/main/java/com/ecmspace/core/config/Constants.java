package com.ecmspace.core.config;

public class Constants {
	
	public static final String SERVICE_GCP = "gcp";
	public static final String SERVICE_AWS = "aws";
	
	public static final String ACTION_CREATEDOCUMENT = "createdocument" ;
	public static final String ACTION_UPDATEDOCUMENT = "updatedocument" ;
	public static final String ACTION_DELETEDOCUMENT = "deletedocument" ;
	
	public static final String GCP_KEY_CONTENT = "content";
	public static final String GCP_KEY_METADATA = "metadata";
	public static final String GCP_KEY_BUCKET_NAME = "bucketname";
	public static final String GCP_KEY_BUCKET_LOCATION = "bucketlocation";
	public static final String GCP_KEY_MERGE_METADATA_FLAG = "mergemetadata";
	public static final String GCP_KEY_FILEPATH = "filepath";
	public static final String GCP_KEY_CONTENT_TYPE = "contenttype";
	
	public static final String GCP_KEY_BLOBNAME = "blobname";
	public static final String GCP_KEY_FOLDERNAME = "foldername";
	public static final String GCP_KEY_STATE = "state";
	
	public static final String DOC_STATE_DRAFT = "Draft";
	public static final String DOC_STATE_APPROVED = "Approved";
	public static final String DOC_STATE_REJECTED = "Rejected";
	
	
	// Flowable constants
	
	public static final String GCP_TARGET_DIR_KEY = "targetDirectory";
	public static final String GCP_DOWNLOAD_MEDIA_LINK = "mediaLink";
	public static final String FLOWABLE_TASK_ID = "taskId";
	public static final String GCP_OBJECT_KEY = "object";
	public static final String DOCUMENT_UPLOAD_PROCESS_NAME="starDocumentUploadFlow";

	

}
