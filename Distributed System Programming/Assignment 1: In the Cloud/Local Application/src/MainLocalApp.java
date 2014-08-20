import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.codec.binary.Base64;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;


public class MainLocalApp {

	public static void main(String[] args) throws IOException {
		
		AWSCredentials credentials = new PropertiesCredentials(MainLocalApp.class.getResourceAsStream("AwsCredentials.properties"));
		AmazonEC2 ec2 = new AmazonEC2Client(credentials);
        AmazonS3 s3 = new AmazonS3Client(credentials);
        AmazonSQS sqs = new AmazonSQSClient(new PropertiesCredentials(
        		SimpleQueueServiceSample.class.getResourceAsStream("AwsCredentials.properties")));
        
        String localID = ""+UUID.randomUUID();
        
       try {
           // check if there's a ManagerTasks queue. if not, create it.
           String managerTasksUrl = "";
           try{
           	managerTasksUrl = sqs.getQueueUrl(new GetQueueUrlRequest("ManagerTasks")).getQueueUrl();
           } catch (QueueDoesNotExistException e){
           	CreateQueueRequest createQueueRequest4 = new CreateQueueRequest("ManagerTasks");
           	managerTasksUrl = sqs.createQueue(createQueueRequest4).getQueueUrl();
           }
    	   
       	    // create bucket on S3
            s3.createBucket(localID);
            
            // upload the images list to S3
            File path = new File(args[0]);
            PutObjectRequest putObjectRequest = new PutObjectRequest(localID, "imageslist", path);
            putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            s3.putObject(putObjectRequest);
            
            // create the DoneJobQueue queue
            CreateQueueRequest createQueueRequest3 = new CreateQueueRequest("DoneJobQueue"+localID);
            String doneJobQueueUrl = sqs.createQueue(createQueueRequest3).getQueueUrl();
            
            // send the Manager the bucket name and n value
            String messageBody = localID+","+args[2];
            sqs.sendMessage(new SendMessageRequest(managerTasksUrl,messageBody));
            
            // check if a manager exists
            List<Instance> instances = new ArrayList<Instance>();
            try {
            	// check if there is a running / stopped manager instance
            	Boolean flag = false;
            	List<Reservation> reservList = ec2.describeInstances().getReservations();
            	for (int i=0 ; i<reservList.size() ; i++){
            		instances = reservList.get(i).getInstances();
            		for (int j=0 ; j<instances.size() ; j++){
            			if (instances.get(j).getTags().size() > 0 && 
            					instances.get(j).getTags().get(0).getValue().equals("Manager") && 
            					instances.get(j).getState().getName().equals("running")){
            				flag = true;
            				System.out.println("LocalApp: using an existing instance.");
            				break;
            			}
            		}
            		if (flag == true) break;
            	}
            	// if not, create a new manager instance
            	if (!flag){
            		RunInstancesRequest request = new RunInstancesRequest("ami-51792c38", 1, 1);
            		request.withUserData(managerUserData());
                    request.setInstanceType(InstanceType.T1Micro.toString());
                    instances = ec2.runInstances(request).getReservation().getInstances();
                    System.out.println("LocalApp: new instance was created.");
                    
                    // tag manager instance
                    String resource = instances.get(0).getInstanceId();
                    CreateTagsRequest createTagsRequest = new CreateTagsRequest();
                    createTagsRequest.withResources(resource).withTags(new Tag("Manager","Manager"));
                    ec2.createTags(createTagsRequest);
            	}
            	
            } catch (AmazonServiceException ase) {
                System.out.println("Caught Exception: " + ase.getMessage());
                System.out.println("Reponse Status Code: " + ase.getStatusCode());
                System.out.println("Error Code: " + ase.getErrorCode());
                System.out.println("Request ID: " + ase.getRequestId());
            }
            
            // read summary message from the done job queue
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(doneJobQueueUrl);
            List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            
            List<String> originalAndNewImageUrls = new ArrayList<String>();
            
            Boolean flag = false; // set status to 'done-task-message not received'
            while (!flag){
            	if (!(messages.isEmpty())){
                    // download text file from S3
                    S3Object object = s3.getObject(new GetObjectRequest(localID,"summary"));
                    originalAndNewImageUrls = S3ObjectToStringList(object);
                    System.out.println("LocalApp: summary file downloaded from S3.");
                    flag = true; // change status to 'done-task-message received'
            	}
            	// update messages
            	messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            }
            
          // create html output file
          String outputName = args[1];
          createHtml(originalAndNewImageUrls, outputName);
          System.out.println("LocalApp: html output file created.");
          
          // delete queues
          sqs.deleteQueue(new DeleteQueueRequest(doneJobQueueUrl));
          
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace){
        	System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
       }
	}
	
	private static String managerUserData() {
        String script = "#!/bin/bash"+"\n"+
        				"cd /"+"\n"+
        				"wget http://efratbucket.s3.amazonaws.com/Manager.jar"+"\n"+
        				"java -jar Manager.jar";
        String str = new String(Base64.encodeBase64(script.getBytes()));
        return str;
	}
	
	private static List<String> S3ObjectToStringList(S3Object object) throws IOException{
		S3ObjectInputStream content = object.getObjectContent();
        BufferedReader in = new BufferedReader(new InputStreamReader(content));
        List<String> imageUrls = new ArrayList<String>();
        String line = null;
        try {
			while((line = in.readLine()) != null){
				imageUrls.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return imageUrls;
	}
	
	private static void createHtml(List<String> urls, String outputName){
		try {
			File outputFilePath = new File(outputName);
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath));
			bw.write("<html>");
	        bw.write("<body>");
	        for (int i=0 ; i<urls.size() ; i++){
	        	String allurls = urls.get(i);
	        	String[] splitUrls = allurls.split(",");
	        	String originalUrl = splitUrls[0];
	        	String newUrl = splitUrls[1];
	        	bw.write("<a href=\""+originalUrl+"\"><image src=\""+newUrl+"\"></a>");
	        }
	        bw.write("</body>");
	        bw.write("</html>");
	        bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}