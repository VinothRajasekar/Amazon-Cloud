import java.io.IOException;
import java.util.Properties;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;


public class project2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		int a = 0;
		String line;
		Properties properties = new Properties();
		properties.load(Publicwatch.class.getResourceAsStream("/AwsCredentials.properties"));
		 
		BasicAWSCredentials bawsc = new BasicAWSCredentials(properties.getProperty("accessKey"), properties.getProperty("secretKey"));
		 
		//Launch an EC2 Client
		AmazonEC2Client amazonEC2Client = new AmazonEC2Client(bawsc);
				 
				//Create Instance Request
				RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		
				 
				//Configure Instance Request
				runInstancesRequest.withImageId("ami-700e4a19")
				.withInstanceType("m1.small")
				.withMinCount(1)
				.withMaxCount(1)
				.withKeyName("vinoth");
		 
				
			   RunInstancesResult runInstancesResult = amazonEC2Client.runInstances(runInstancesRequest);  
			   com.amazonaws.services.ec2.model.Instance instance=runInstancesResult.getReservation().getInstances().get(0);

			   Thread.sleep(2*60*1000); 	
			    

	}

}
