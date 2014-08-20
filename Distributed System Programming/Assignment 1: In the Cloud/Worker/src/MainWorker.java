import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class MainWorker {

	public static void main(String[] args) throws IOException {
		
		AWSCredentials credentials = new PropertiesCredentials(MainWorker.class.getResourceAsStream("AwsCredentials.properties"));
		AmazonS3 s3 = new AmazonS3Client(credentials);
        AmazonSQS sqs = new AmazonSQSClient(new PropertiesCredentials(
        		SimpleQueueServiceSample.class.getResourceAsStream("AwsCredentials.properties")));
        
        // find this worker's instance ID
        URL instanceIDurl = new URL("http://169.254.169.254/latest/meta-data/instance-id");
        Scanner s = new Scanner(instanceIDurl.openStream(), "UTF-8");
        String instanceID = s.useDelimiter("\\A").next();
        s.close();
        
        // receive image task messages from the manager
        String workerTasksUrl = sqs.getQueueUrl(new GetQueueUrlRequest("WorkerTasks")).getQueueUrl();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(workerTasksUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        
        String workerResultsUrl = sqs.getQueueUrl(new GetQueueUrlRequest("WorkerResults")).getQueueUrl();
        
        while (!(messages.isEmpty())){
        	// read message
        	String message = messages.get(0).getBody();
        	String[] parsedMessage = message.toString().split(",");
        	String localID = parsedMessage[0];
        	String imageUrl = parsedMessage[1];
        	
        	// delete message from task queue
        	String messageRecieptHandle = messages.get(0).getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(workerTasksUrl, messageRecieptHandle));
            
        	try{
        		// get original image from url
        		BufferedImage originalImage = ImageIO.read(new URL(imageUrl));
        		
        		String timeStamp = new java.util.Date().toString();
        		String text = "efrat, yuval : " + instanceID + " : " + timeStamp;
        		System.out.println("Worker: text = "+text);
        		
        		// add text to image
        		BufferedImage newImage = addText(originalImage, text);
                File file = new File(""+UUID.randomUUID()+".png");
                ImageIO.write(newImage,"png",file);
                
				// upload the new image to S3.
                PutObjectRequest putObjectRequest = new PutObjectRequest(localID, file.getName(), file);
                putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                s3.putObject(putObjectRequest);
                String newImageUrl = "https://s3.amazonaws.com/"+localID+"/"+file.getName();
               
                // send a done image task message to the manager
                String messageBody = localID+"="+imageUrl+","+newImageUrl;
                sqs.sendMessage(new SendMessageRequest(workerResultsUrl,messageBody));
                
                // update the messages list
                messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
                
        	}catch(IOException e){
        		e.printStackTrace();
        	}
        }
	}
	
	private static BufferedImage addText(BufferedImage originalImage, String text){
		BufferedImage newImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = newImage.createGraphics();
		g2d.drawImage(originalImage, 0, 0, 500, 500, null); //resize image
		g2d.setColor(Color.red);
	    g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (newImage.getWidth()-fm.stringWidth(text))/2;
        int y = fm.getHeight();
        g2d.drawString(text, x, y);
        g2d.dispose();
		return newImage;
	}
}