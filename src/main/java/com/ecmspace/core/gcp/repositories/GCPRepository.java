package com.ecmspace.core.gcp.repositories;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.ecmspace.core.config.Constants;
import com.ecmspace.core.flowable.FlowableService;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;


@Repository
@Qualifier("gcpRepository")
public class GCPRepository {

	@Autowired
	private Storage storageInstace;
	
	@Autowired
	private FlowableService flowableService;
	

	@Value("${gcp.projectid}")
	private String project_id;

	public String createBucket(Map<String, Object> params) {

		Bucket newBucket = new Bucket();
		newBucket.setName((String)params.get(Constants.GCP_KEY_BUCKET_NAME));
		newBucket.setLocation((String)params.get(Constants.GCP_KEY_BUCKET_LOCATION));

		try {

			Storage.Buckets.Insert insertRequest = storageInstace.buckets().insert(project_id, newBucket);
			insertRequest.setProjection("full");
			Bucket created = insertRequest.execute();
			return created.getName();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public String updateBucket(Map<String, Object> params) {

		String bucketName = (String)params.get(Constants.GCP_KEY_BUCKET_NAME);
		Bucket newBucket = new Bucket();
		newBucket.setName((String)params.get(Constants.GCP_KEY_BUCKET_NAME));
		//newBucket.setVersioning(Versioning)

		try {
			Storage.Buckets.Update updateRequest = storageInstace.buckets().update(bucketName, newBucket);
			updateRequest.setProjection("full");
			return updateRequest.execute().getName();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Bucket getBucket(String bucketName) {

		Storage.Buckets.Get bucketRequest;
		try {
			bucketRequest = storageInstace.buckets().get(bucketName);
			bucketRequest.setProjection("full");
			return bucketRequest.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Fetch the full set of the bucket's properties (e.g. include the ACLs in the response)

		return null;
	}



	public boolean deleteBucket(String bucketName) {
		Storage.Buckets.Delete deleteRequest;
		try {
			deleteRequest = storageInstace.buckets().delete(bucketName);
			deleteRequest.execute();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String createBlob(Map<String, Object> params) {
		

		String bucketName = (String)params.get(Constants.GCP_KEY_BUCKET_NAME);
		String blobName = (String)params.get(Constants.GCP_KEY_BLOBNAME);
		String file = (String)params.get(Constants.GCP_KEY_FILEPATH);
		String contentType = (String)params.get(Constants.GCP_KEY_CONTENT_TYPE);
		
		Map<String,Object> wflwVariables = new HashMap<String,Object>();
		wflwVariables.put(Constants.GCP_TARGET_DIR_KEY, blobName);
		
		blobName = "Staging/"+blobName.substring(blobName.lastIndexOf("/")+1);
		
		wflwVariables.put(Constants.GCP_KEY_BUCKET_NAME, bucketName);
		wflwVariables.put(Constants.GCP_KEY_BLOBNAME, blobName);
		

		Map<String, String> metadata =  (Map<String, String>) params.get(Constants.GCP_KEY_METADATA);
		metadata.put(Constants.GCP_KEY_STATE,Constants.DOC_STATE_DRAFT);

		StorageObject obj = new StorageObject()
				.setName(blobName)
				.setMetadata(metadata)
				// Set the access control list to publicly read-only
				.setAcl(Arrays.asList(
						new ObjectAccessControl().setEntity("allUsers").setRole("READER")));
		
		/*if(metadata != null){
			Iterator<String> itr= metadata.keySet().iterator();
			while(itr.hasNext()){
				String key = itr.next();
				obj.set(key, metadata.get(key));
			}
		}*/

		//obj.set(Constants.GCP_KEY_STATE,Constants.DOC_STATE_DRAFT);

		Storage.Objects.Insert insertRequest;
		try {
			InputStreamContent contentStream = new InputStreamContent(contentType, new FileInputStream(file));
			contentStream.setLength(file.length());
			insertRequest = storageInstace.objects().insert(bucketName, obj, contentStream);
			StorageObject retObj = insertRequest.execute();
			wflwVariables.put(Constants.GCP_DOWNLOAD_MEDIA_LINK, retObj.getMediaLink());
			flowableService.beginDocumentUploadProcess(file,contentType,wflwVariables);
			
			return retObj.getName();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}


	public String updateBlob(Map<String, Object> params) {

		String bucketName = (String)params.get(Constants.GCP_KEY_BUCKET_NAME);
		String blobName = (String)params.get(Constants.GCP_KEY_BLOBNAME);

		Map<String, String> metadata =  (Map<String, String>) params.get(Constants.GCP_KEY_METADATA);

		StorageObject obj = new StorageObject()
				.setName(blobName);

		Iterator<String> itr= metadata.keySet().iterator();
		while(itr.hasNext()){
			String key = itr.next();
			obj.set(key, metadata.get(key));
		}

		Storage.Objects.Update updateRequest = null;

		try {
			updateRequest = storageInstace.objects().update(bucketName, blobName, obj);
			return updateRequest.execute().getName();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String promoteDocument(String taskId){
		
		Map<String,Object> processVariables = flowableService.getProcessVariables(taskId);
		
		String sourceBucket = (String)processVariables.get(Constants.GCP_KEY_BUCKET_NAME);
		String sourceObject = (String)processVariables.get(Constants.GCP_KEY_BLOBNAME);
		String destinationObject = (String) processVariables.get(Constants.GCP_TARGET_DIR_KEY);
		
		System.out.println("sourceBucket: "+sourceBucket);
		System.out.println("sourceObject: "+sourceObject);
		System.out.println("destinationObject: "+destinationObject);
		
		StorageObject doc = this.getBlob(sourceBucket,sourceObject);
		
		//String targetName = (String) processVariables.get(Constants.GCP_TARGET_DIR_KEY);
		//doc.setName(targetName);
		
		Map<String, String> metadata = doc.getMetadata();
		metadata.put(Constants.GCP_KEY_STATE,Constants.DOC_STATE_APPROVED);
		doc.setMetadata(metadata);
		//doc.set(Constants.GCP_KEY_STATE,Constants.DOC_STATE_APPROVED);
		
		//Storage.Objects.Update updateRequest = null;
		Storage.Objects.Copy copyRequest= null;
		
		try {
			copyRequest = storageInstace.objects().copy(sourceBucket, sourceObject, sourceBucket, destinationObject, doc);
			StorageObject promotedDocument = copyRequest.execute();
			System.out.println("Promoted document to : "+promotedDocument.getName());
			this.deleteBlob(sourceBucket, sourceObject);
			System.out.println("Deleted document from staging..");
			//updateRequest = storageInstace.objects().update(doc.getBucket(), targetName, doc);
			return promotedDocument.getName();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}
	
	public String rejectDocument(String taskId){
		
		Map<String,Object> processVariables = flowableService.getProcessVariables(taskId);
		
		String sourceBucket = (String)processVariables.get(Constants.GCP_KEY_BUCKET_NAME);
		String sourceObject = (String)processVariables.get(Constants.GCP_KEY_BLOBNAME);
		
		StorageObject doc = this.getBlob(sourceBucket,sourceObject);
		
		String targetName = "Rejected/"+doc.getName().substring(doc.getName().lastIndexOf("/")+1);
		
		doc.setName(targetName);
		Map<String, String> metadata = doc.getMetadata();
		metadata.put(Constants.GCP_KEY_STATE,Constants.DOC_STATE_REJECTED);
		doc.setMetadata(metadata);
		
		
		Storage.Objects.Copy copyRequest= null;
		
		

		try {
			copyRequest = storageInstace.objects().copy(sourceBucket, sourceObject, sourceBucket, targetName, doc);
			StorageObject rejectedDocument = copyRequest.execute();
			System.out.println("Rejected "+rejectedDocument.getName());
			this.deleteBlob(sourceBucket, sourceObject);
			System.out.println("Deleted document from staging..");
			//updateRequest = storageInstace.objects().update(doc.getBucket(), targetName, doc);
			return rejectedDocument.getName();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}


	public boolean deleteBlob(String bucketName, String blobName) {
		Storage.Objects.Delete deleteRequest;
		try {
			deleteRequest = storageInstace.objects().delete(bucketName, blobName);
			deleteRequest.execute();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<StorageObject> listAllBlobs(String bucketName, String folderPath) {

		List<StorageObject> results = new ArrayList<StorageObject>();

		Storage.Objects.List listRequest;
		try {
			if(folderPath != null && (folderPath.isEmpty() == false))
				listRequest = storageInstace.objects().list(bucketName).setDelimiter("/").setPrefix(folderPath);
			else
				listRequest = storageInstace.objects().list(bucketName).setDelimiter("/");
			Objects objects;
			// Iterate through each page of results, and add them to our results list.
			do {
				objects = listRequest.execute();
				// Add the items in this page of results to the list we'll return.
				results.addAll(objects.getItems());
				if(folderPath != null && (folderPath.isEmpty()==false))
					results.remove(0);
				// Get the next page, in the next iteration of this loop.
				listRequest.setPageToken(objects.getNextPageToken());
			} while (null != objects.getNextPageToken());

		} catch (IOException e) {
			e.printStackTrace();
		}

		return results;
	}

	public StorageObject getBlob(String bucketName, String blobName) {

		try {
			Storage.Objects.Get getRequest = storageInstace.objects().get(bucketName, blobName);
			return getRequest.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String createFolder(Map<String, Object> params) {

		String bucketName = (String)params.get(Constants.GCP_KEY_BUCKET_NAME);
		String folderName = (String)params.get(Constants.GCP_KEY_FOLDERNAME);

		Map<String, String> metadata =  (Map<String, String>) params.get(Constants.GCP_KEY_METADATA);

		StorageObject obj = new StorageObject()
				.setName(folderName+"/")
				// Set the access control list to publicly read-only
				.setAcl(Arrays.asList(
						new ObjectAccessControl().setEntity("allUsers").setRole("READER")));
		if(metadata != null){
			Iterator<String> itr= metadata.keySet().iterator();
			while(itr.hasNext()){
				String key = itr.next();
				obj.set(key, metadata.get(key));
			}
		}



		Storage.Objects.Insert insertRequest;
		try {
			InputStream empty = new ByteArrayInputStream(new byte[0]);
			InputStreamContent contentStream = new InputStreamContent("application/x-directory", empty);
			contentStream.setLength(0);
			insertRequest = storageInstace.objects().insert(bucketName, obj, contentStream);
			return insertRequest.execute().getName();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public List<String> listAllFolders(String bucketName, String folderPath) {

		List<String> results = new ArrayList<String>();
		Storage.Objects.List listRequest;

		try {
			if(folderPath != null && (folderPath.isEmpty() == false))
				listRequest = storageInstace.objects().list(bucketName).setDelimiter("/").setPrefix(folderPath);
			else
				listRequest = storageInstace.objects().list(bucketName).setDelimiter("/");
			Objects objects;
			// Iterate through each page of results, and add them to our results list.
			do {
				objects = listRequest.execute();
				// Add the items in this page of results to the list we'll return.
				results.addAll(objects.getPrefixes());
				// Get the next page, in the next iteration of this loop.
				listRequest.setPageToken(objects.getNextPageToken());
			} while (null != objects.getNextPageToken());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return results;
	}

	public StorageObject getFolder(String bucketName, String folderName) {
		try {
			Storage.Objects.Get getRequest = storageInstace.objects().get(bucketName, folderName+"/");
			return getRequest.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}


}
