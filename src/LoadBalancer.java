import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.elasticbeanstalk.model.Listener;
import com.amazonaws.services.elasticbeanstalk.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerListenersRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.EnableAvailabilityZonesForLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult;
import com.amazonaws.services.route53.model.CreateHealthCheckRequest;
import com.amazonaws.services.route53.model.HealthCheckConfig;

public class LoadBalancer {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */

	@SuppressWarnings("unchecked")
	String s;

	public static void main(String[] args) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		String line;
		Properties properties = new Properties();
		properties.load(Publicwatch.class
				.getResourceAsStream("/AwsCredentials.properties"));

		BasicAWSCredentials bawsc = new BasicAWSCredentials(
				properties.getProperty("accessKey"),
				properties.getProperty("secretKey"));

		// Launch an EC2 Client
		AmazonEC2Client amazonEC2Client = new AmazonEC2Client(bawsc);

		AmazonElasticLoadBalancingClient lb = new AmazonElasticLoadBalancingClient(
				bawsc);

		CreateLoadBalancerRequest br = new CreateLoadBalancerRequest();
		CreateLoadBalancerListenersRequest lr = new CreateLoadBalancerListenersRequest();
		List<com.amazonaws.services.elasticloadbalancing.model.Listener> ld = lr
				.getListeners();

		ld.add(new com.amazonaws.services.elasticloadbalancing.model.Listener(
				"HTTP", 80, 80));
		ld.add(new com.amazonaws.services.elasticloadbalancing.model.Listener(
				"HTTP", 8080, 8080));

		br.setLoadBalancerName("DemoA");
		br.withAvailabilityZones("us-east-1d");
		br.setListeners(ld);

		CreateLoadBalancerResult lres = lb.createLoadBalancer(br);
		System.out.println("created load balancer loader");

		HealthCheck ha = new HealthCheck();
		ha.setHealthyThreshold(10);
		ha.setUnhealthyThreshold(2);
		ha.setInterval(20);
		ha.setTarget("HTTP:8080/upload");
		ha.setTimeout(5);

		ConfigureHealthCheckRequest hcr = new ConfigureHealthCheckRequest();
		hcr.withHealthCheck(ha);
		hcr.withLoadBalancerName("DemoA");

		ConfigureHealthCheckResult confChkResult = lb.configureHealthCheck(hcr);
		// confChkResult.withHealthCheck(ha);

		// CreateHealthCheckRequest hr = new CreateHealthCheckRequest();
		// hr.getHealthCheckConfig();
		System.out.println("helath:" + ha.getTarget());

		DescribeLoadBalancersRequest lreq = new DescribeLoadBalancersRequest();
		lreq.withLoadBalancerNames("DemoA");

		DescribeLoadBalancersResult res = lb.describeLoadBalancers(lreq);

		com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription des = res
				.getLoadBalancerDescriptions().get(0);

		String ini = des.getDNSName();

		System.out.println("conf:" + ini);

		int a = 0;
		int count = 0;
		int c=0;
		String line1 = "";
		do {
			Placement pl = new Placement();
			pl.withAvailabilityZone("us-east-1d");

			// Configure Instance Request
			RunInstancesRequest runInstancesRequest1 = new RunInstancesRequest();
			runInstancesRequest1.withImageId("ami-700e4a19")
					.withInstanceType("m1.small").withMinCount(1)
					.withMaxCount(1).withPlacement(pl).withKeyName("vinoth");

			RunInstancesResult runInstancesResult = amazonEC2Client
					.runInstances(runInstancesRequest1);
			Thread.sleep(2 * 60 * 1000);

			RegisterInstancesWithLoadBalancerRequest ri = new RegisterInstancesWithLoadBalancerRequest();

			com.amazonaws.services.ec2.model.Instance instance = runInstancesResult
					.getReservation().getInstances().get(0);
			String instances = instance.getInstanceId();
			List<Instance> Instanceid = new ArrayList<Instance>();
			Instanceid
					.add(new com.amazonaws.services.elasticloadbalancing.model.Instance(
							instances));
			ri.setLoadBalancerName("DemoA");
			ri.setInstances(Instanceid);

			System.out.println("i:" + Instanceid);

			@SuppressWarnings("unused")
			RegisterInstancesWithLoadBalancerResult registerWithLoadBalancerResult = lb
					.registerInstancesWithLoadBalancer(ri);
			Thread.sleep(2 * 60 * 1000);
			Process p = Runtime.getRuntime().exec(
					"/home/ubuntu/benchmark/apache_bench.sh sample.jpg 100000 100 "
							+ ini + " logfile");
			count = count + 1;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((line = in.readLine()) != null) {
				System.out.println("output:" + line);
				line1 = line.toString();

				if (line1.length() > 19) {

					if (line1.substring(0, 19).equalsIgnoreCase(
							"Requests per second")
							&& line1.length() > 19) {

						if (line1.substring(27, 28).contains(".")) {
							c = Integer.parseInt(line1.substring(24, 27));
							System.out.println("Request per second" + c);

						} else {

							c = Integer.parseInt(line1.substring(24, 28));
							System.out.println("Request per second" + c);
						}
					}
				}
			}
			in.close();
			p.waitFor();
		} while (c <= 901);

		System.out
				.println("Total number of instances required:" + "  " + count);

	}

}
