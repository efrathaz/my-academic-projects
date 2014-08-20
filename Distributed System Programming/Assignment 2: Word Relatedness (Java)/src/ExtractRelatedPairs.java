import java.io.IOException;
import java.util.ArrayList;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;
import com.amazonaws.services.elasticmapreduce.model.HadoopJarStepConfig;
import com.amazonaws.services.elasticmapreduce.model.JobFlowInstancesConfig;
import com.amazonaws.services.elasticmapreduce.model.PlacementType;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowRequest;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowResult;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;


public class ExtractRelatedPairs {

	public static void main(String[] args) throws IOException {
		
		AWSCredentials credentials = new PropertiesCredentials(ExtractRelatedPairs.class.getResourceAsStream("AwsCredentials.properties"));
		AmazonElasticMapReduce mapReduce = new AmazonElasticMapReduceClient(credentials);
		
		// Step 1
		HadoopJarStepConfig hadoopJarStep1 = new HadoopJarStepConfig()
		    .withJar("s3n://efratbucket/Step1.jar")
		    .withArgs("s3://datasets.elasticmapreduce/ngrams/books/20090715/eng-gb-all/5gram/data","s3n://efratbucket/output/A");
		
		StepConfig stepConfig1 = new StepConfig()
			.withName("Step1")
			.withHadoopJarStep(hadoopJarStep1)
			.withActionOnFailure("TERMINATE_JOB_FLOW");
		
		// Step 2
		HadoopJarStepConfig hadoopJarStep2 = new HadoopJarStepConfig()
			.withJar("s3n://efratbucket/Step2.jar")
			.withArgs("s3n://efratbucket/output/A","s3n://efratbucket/output/B");
	
		StepConfig stepConfig2 = new StepConfig()
			.withName("Step2")
			.withHadoopJarStep(hadoopJarStep2)
			.withActionOnFailure("TERMINATE_JOB_FLOW");
		
		// Step 3
		HadoopJarStepConfig hadoopJarStep3 = new HadoopJarStepConfig()
			.withJar("s3n://efratbucket/Step3.jar")
			.withArgs("s3n://efratbucket/output/B","s3n://efratbucket/output/C");

		StepConfig stepConfig3 = new StepConfig()
			.withName("Step3")
			.withHadoopJarStep(hadoopJarStep3)
			.withActionOnFailure("TERMINATE_JOB_FLOW");
		
		// Step 4
		HadoopJarStepConfig hadoopJarStep4 = new HadoopJarStepConfig()
		.withJar("s3n://efratbucket/Step4.jar")
		.withArgs("s3n://efratbucket/output/C","s3n://efratbucket/output/FinalOutput",args[0]);

		StepConfig stepConfig4 = new StepConfig()
			.withName("Step4")
			.withHadoopJarStep(hadoopJarStep4)
			.withActionOnFailure("TERMINATE_JOB_FLOW");
		
		ArrayList<StepConfig> steps = new ArrayList<StepConfig>();
		steps.add(stepConfig1);
		steps.add(stepConfig2);
		steps.add(stepConfig3);
		steps.add(stepConfig4);
		
		JobFlowInstancesConfig instances = new JobFlowInstancesConfig()
		    .withInstanceCount(19)
		    .withMasterInstanceType(InstanceType.M1Medium.toString())
		    .withSlaveInstanceType(InstanceType.M1Medium.toString())
		    .withHadoopVersion("0.20")
		    .withEc2KeyName("efrathaz")
		    .withKeepJobFlowAliveWhenNoSteps(false)
		    .withPlacement(new PlacementType("us-east-1b"));
		
		RunJobFlowRequest runFlowRequest = new RunJobFlowRequest()
		    .withName("ExtractRelatedPairs")
		    .withInstances(instances)
		    .withSteps(steps)
		    .withLogUri("s3n://efratbucket/Logs/").withAmiVersion("1.0.0");
		 
		RunJobFlowResult runJobFlowResult = mapReduce.runJobFlow(runFlowRequest);
		String jobFlowId = runJobFlowResult.getJobFlowId();
		System.out.println("Ran job flow with id: " + jobFlowId);
	}

}
