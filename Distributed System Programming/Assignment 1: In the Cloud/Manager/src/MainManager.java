import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.codec.binary.Base64;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
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
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class MainManager {

	public static void main(String[] args) throws IOException {
		
		AWSCredentials credentials = new PropertiesCredentials(MainManager.class.getResourceAsStream("AwsCredentials.properties"));
		AmazonEC2 ec2 = new AmazonEC2Client(credentials);
        AmazonS3 s3 = new AmazonS3Client(credentials);
        AmazonSQS sqs = new AmazonSQSClient(new PropertiesCredentials(
        		SimpleQueueServiceSample.class.getResourceAsStream("AwsCredentials.properties")));

        // receive message from a local application
        String managerTasksUrl = sqs.getQueueUrl(new GetQueueUrlRequest("ManagerTasks")).getQueueUrl();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(managerTasksUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        
        // create the WorkerTasks queue
        CreateQueueRequest createQueueRequest1 = new CreateQueueRequest("WorkerTasks");
        String workerTasksUrl = sqs.createQueue(createQueueRequest1).getQueueUrl();
        
        // create the WorkerResults queue
        CreateQueueRequest createQueueRequest2 = new CreateQueueRequest("WorkerResults");
        String workerResultsUrl = sqs.createQueue(createQueueRequest2).getQueueUrl();
        
        while(true){
            if (!(messages.isEmpty())){
            	// read task message
            	String message = messages.get(0).getBody();
                String[] parsedMessage = message.toString().split(",");
                String localID = parsedMessage[0];
                String bucketName = localID;
                int n = Integer.parseInt(parsedMessage[1]);
                
                // delete message from ManagerTasks queue
                String messageRecieptHandle = messages.get(0).getReceiptHandle();
                sqs.deleteMessage(new DeleteMessageRequest(managerTasksUrl, messageRecieptHandle));
                
                // download the url list from S3
                S3Object object = s3.getObject(new GetObjectRequest(bucketName,"imageslist"));
                List<String> imageUrls = S3ObjectToStringList(object);
                
                // create an image task message for every url and send it to the WorkerTasks queue
                for (int i=0 ; i<imageUrls.size() ; i++){
                	String messageBody = localID+","+imageUrls.get(i);
                	sqs.sendMessage(new SendMessageRequest(workerTasksUrl,messageBody));
                }
                
                // check how many workers are there
                List<Reservation> reservList = ec2.describeInstances().getReservations();
                int activeWorkers = 0;
                for (int i=0 ; i<reservList.size() ; i++){
                	List<Instance> instances = reservList.get(i).getInstances();
            		for (int j=0 ; j<instances.size() ; j++){
            			if (instances.get(j).getTags().size() > 0 &&
            					instances.get(j).getTags().get(0).getValue().equals("Worker") &&
            					instances.get(j).getState().getName().equals("running")){
            				activeWorkers++;
            			}
            		}
                }
                
                // create more worker nodes
                int numOfWorkers = (imageUrls.size() / n);
                if(numOfWorkers > activeWorkers){
                	numOfWorkers = numOfWorkers - activeWorkers;
                }
                createWorkers(ec2,numOfWorkers);
                
                // check if the workers are done
                List<String> originalAndNewImageUrls = new ArrayList<String>();
                while (originalAndNewImageUrls.size() < imageUrls.size()){ // do as long as there are new urls to add to the newImageUrls list
                	// check the WorkerResults queue for messages
                	ReceiveMessageRequest receiveMessageRequest1 = new ReceiveMessageRequest(workerResultsUrl);
                	List<Message> resultMessages = sqs.receiveMessage(receiveMessageRequest1).getMessages();
                	
                	// read message
                	if (!(resultMessages.isEmpty())){
                		for (int i=0 ; i<resultMessages.size() ; i++){
                			// if the message is for this particular Local application,
                			// add the new url image to the originalAndNewImageUrls list and delete the message
                			boolean rightLocalID = readResultMessages(resultMessages.get(i),originalAndNewImageUrls,localID);
                			
                			if (rightLocalID){
                				// delete message from result queue
                    			String messageRecieptHandle1 = resultMessages.get(i).getReceiptHandle();
                                sqs.deleteMessage(new DeleteMessageRequest(workerResultsUrl, messageRecieptHandle1));
                			}
                		}
                	}
                }
                
                // create summary file and upload it to S3
                File summary = new File("summary.txt");
                try {
                    BufferedWriter output = new BufferedWriter(new FileWriter(summary));
                    for (int i=0 ; i<originalAndNewImageUrls.size() ; i++){
                    	output.write(originalAndNewImageUrls.get(i)+"\n");
                    }
                    output.close();
                } catch ( IOException e ) {
                	e.printStackTrace();
                }
                s3.putObject(new PutObjectRequest(localID,"summary",summary));
                
                // send 'done task' message to the local application with file location in S3
                String doneJobQueueUrl = sqs.getQueueUrl(new GetQueueUrlRequest("DoneJobQueue"+localID)).getQueueUrl();
                String messageBody = localID+",summary";
                sqs.sendMessage(new SendMessageRequest(doneJobQueueUrl,messageBody));
            }
            // update the messages list
            messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        }
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
	
	private static boolean readResultMessages(Message m, List<String> originalAndNewImageUrls, String myLocalID){
		String message = m.getBody();
		String[] parsedMessage = message.toString().split("=");
		String localID = parsedMessage[0];
		String imageurls = parsedMessage[1];
		if (localID.equals(myLocalID)){
			originalAndNewImageUrls.add(imageurls);
			return true;
		}
		return false;
	}

	private static void createWorkers(AmazonEC2 ec2, int number){
		RunInstancesRequest request = new RunInstancesRequest("ami-51792c38", number, number);
        request.setInstanceType(InstanceType.T1Micro.toString());
        request.withUserData(workerUserData());
        List<Instance> instances = ec2.runInstances(request).getReservation().getInstances();
        List<String> resources = new ArrayList<String>();
        for(int i=0 ; i<instances.size() ; i++){
        	resources.add(instances.get(i).getInstanceId());
        }
        CreateTagsRequest createTagsRequest = new CreateTagsRequest();
        createTagsRequest.withResources(resources).withTags(new Tag("Worker","Worker"));
        ec2.createTags(createTagsRequest);
	}
	
	private static String workerUserData() {
		String script = "#!/bin/bash"+"\n"+
						"cd /"+"\n"+
						"wget http://efratbucket.s3.amazonaws.com/Worker.jar"+"\n"+
						"java -jar Worker.jar";
        String str = new String(Base64.encodeBase64(script.getBytes()));
        return str;
	}
}