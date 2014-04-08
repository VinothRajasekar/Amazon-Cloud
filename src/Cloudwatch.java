import java.io.IOException;
import java.util.Properties;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.EnableAlarmActionsRequest;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.identitymanagement.model.ListAccountAliasesRequest;


public class Cloudwatch {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Properties properties = new Properties();
		properties.load(Cloudwatch.class.getResourceAsStream("/AwsCredentials.properties"));
		 
		BasicAWSCredentials bawsc = new BasicAWSCredentials(properties.getProperty("accessKey"), properties.getProperty("secretKey"));
		 
		AmazonCloudWatchClient client = new AmazonCloudWatchClient(bawsc);

		

	}

}
