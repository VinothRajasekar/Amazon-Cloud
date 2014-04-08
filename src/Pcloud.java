import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.autoscaling.model.Instance;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.opsworks.model.DescribeInstancesResult;


public class Pcloud {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		Properties properties = new Properties();
		properties.load(Pcloud.class.getResourceAsStream("/AwsCredentials.properties"));
		 
		BasicAWSCredentials bawsc = new BasicAWSCredentials(properties.getProperty("accessKey"), properties.getProperty("secretKey"));
		 
		//Create an Amazon EC2 Client
		AmazonEC2Client ec2 = new AmazonEC2Client(bawsc);
		 
		//Create Instance Request
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		 
		//Configure Instance Request
		runInstancesRequest.withImageId("ami-700e4a19")
		.withInstanceType("m1.small")
		.withMinCount(1)
		.withMaxCount(1)
		.withKeyName("vinoth");
		//.withSecurityGroups("SSH-only");
		 
		//Launch Instance
		RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);  
		 
		//Return the Object Reference of the Instance just Launched
		com.amazonaws.services.ec2.model.Instance instance=runInstancesResult.getReservation().getInstances().get(0);


		System.out.println(instance.toString());
		Thread.sleep(2*60*1000); 
		System.out.println(instance.toString());
		DescribeInstanceStatusRequest iR = new DescribeInstanceStatusRequest().withInstanceIds(instance.getInstanceId());
		DescribeInstanceStatusResult describeInstanceResult = ec2.describeInstanceStatus(iR);
		List<InstanceStatus> state = describeInstanceResult.getInstanceStatuses();
		System.out.println("size:" + state.size());
		while (state.size() < 2) { 
		    // Do nothing, just wait, have thread sleep if needed
			System.out.println("sizea:" + state.size());
			System.out.println("Hello:" + instance.getPublicDnsName());
		    describeInstanceResult = ec2.describeInstanceStatus(iR);
		    state = describeInstanceResult.getInstanceStatuses();
		    Process p = Runtime.getRuntime().exec("sudo /home/ubuntu/benchmark/apache_bench.sh sample.jpg 100000 100 "+instance.getPublicDnsName()+" logfile");
		    System.out.println("Hello1:" + p);  
		}
		String status = state.get(0).getInstanceState().getName(); 
		System.out.println("hell:" + status);
		System.out.println(instance.getPublicDnsName());
		
		
		//Process p = Runtime.getRuntime().exec(instance.getPublicIpAddress());
		
		System.out.println("Hello:" + instance.getPublicDnsName());
		
		//Process p = Runtime.getRuntime().exec("/home/ubuntu/benchmark/apache_bench.sh sample.jpg 100000 10 "+instance.getPublicIpAddress()+" logfile");

		//System.out.println(sb);
		
		//Thread.sleep(2*60*1000); 
		//BufferedInputStream c= new BufferedInputStream(p.getInputStream());
		System.out.println("state:" + instance.getState());
	
		
		
		

	}

}
