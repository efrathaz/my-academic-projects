ASSIGNMENT DESCRIPTION:

This program distributively adds text to a list of images.
The application is composed of a local application and instances running in the Amazon cloud.
The application gets as an input a text file containing a list of URLs of images. 
Then, instances are launched in AWS (workers). Each worker downloads image files, 
adds text to these images, and displays the images after adding the text on a webpage.

System Architecture:

1. Local Application - The application resides on a local (non-cloud) machine. Once started, it reads the containing the image files from the user. It then:
  * Checks if a Manager node is active on the EC2 cloud. If not, the application starts the manager node.
  * The application uploads the file with the list of images to S3.
  * The application sends a message to a specified SQS queue, stating the location of the images list on S3.
  * The application checks a specified SQS queue for a message indicating the process is done and the response is available on S3.
  * The application downloads the response from S3.

2. Manager - The manager process resides on an EC2 node. It checks a special SQS queue for a message indicating an image list is waiting on S3. 
  Once it receives the message it:
  * Downloads the image list from S3.
  * Creates an SQS message for each URL in the image list.
  * Checks the SQS message count and starts Worker processes (nodes) accordingly.
  Once the image queue count is 0, the manager should:
  * Read all the messages from the results queue,
  * Create the output file accordingly,
  * Upload the output file to S3,
  * Send a message to the user queue with the location of the file.

3. Worker - A worker process resides on an EC2 node. Its life cycle is as follows:
  Repeatedly:
  * Gets an image message from an SQS queue.
  * Downloads the image file indicated in the message.
  * Adds text to the image.
  * Uploads the resulting image file to S3.
  * Puts a message in an SQS queue indicating the original URL of the image and the S3 url of the new image file.
  * Removes the image message from the SQS queue.
  
In order to run the program you need to follow these steps:
 1. Creat a runnable jar file for the Local Application, the Manager and the Worker.
 2. Upload the jar files to S3.
 3. Run the Local Application jar file with parameters: imageurls.txt, output.html, n (n being the number of pictures per worker).