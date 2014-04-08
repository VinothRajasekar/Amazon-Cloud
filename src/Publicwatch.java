
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.autoscaling.model.Instance;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.elasticbeanstalk.model.Listener;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;


public class Publicwatch {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
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
				Thread.sleep(2*60*1000); 		
		//Obtain a list of Reservations
		List<Reservation> reservations = amazonEC2Client.describeInstances().getReservations();
		 
		     
		int reservationCount = reservations.size();
		
		
		 
		for(int i = 0; i < reservationCount; i++) {
		    List<com.amazonaws.services.ec2.model.Instance> instances = reservations.get(i).getInstances();
		 
		    int instanceCount = instances.size();
		     
		    //Print the instance IDs of every instance in the reservation.
		    for(int j = 0; j < instanceCount; j++) {
		        com.amazonaws.services.ec2.model.Instance instance = instances.get(j);
		        //System.out.println("hell0:" + instance.getPublicDnsName());
		 
		        if(instance.getState().getName().equals("running")  && instance.getInstanceType().equals("m1.small")){
		        	
		            System.out.println(instance.getInstanceId());
		            System.out.println("hell:" + instance.getPublicDnsName());
				do
		           {
		           Process p = Runtime.getRuntime().exec("/home/ubuntu/benchmark/apache_bench.sh sample.jpg 100000 100 "+instance.getPublicDnsName()+" logfile");
		           BufferedReader in = new BufferedReader(
		                   new InputStreamReader(p.getInputStream()) );
		           while ((line = in.readLine()) != null) {
		             System.out.println("output:" + line);
		           }
		           in.close();
				   a++;
				   p.waitFor();
		           }while (a<10);
		           
		        }
		    }
		}


	}

}
