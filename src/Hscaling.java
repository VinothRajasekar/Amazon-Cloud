import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.CreateAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.CreateLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.DeleteLaunchConfigurationRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingNotificationTypesRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingNotificationTypesResult;
import com.amazonaws.services.autoscaling.model.InstanceMonitoring;
import com.amazonaws.services.autoscaling.model.NotificationConfiguration;
import com.amazonaws.services.autoscaling.model.PutNotificationConfigurationRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyRequest;
import com.amazonaws.services.autoscaling.model.PutScalingPolicyResult;
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupResult;
import com.amazonaws.services.autoscaling.model.UpdateAutoScalingGroupRequest;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.cloudwatch.model.transform.MetricStaxUnmarshaller;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckResult;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerListenersRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.identitymanagement.model.CreateGroupResult;
import com.amazonaws.services.simpleemail.model.NotificationType;

public class Hscaling {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub

		Properties properties = new Properties();
		properties.load(Publicwatch.class
				.getResourceAsStream("/AwsCredentials.properties"));

		BasicAWSCredentials bawsc = new BasicAWSCredentials(
				properties.getProperty("accessKey"),
				properties.getProperty("secretKey"));

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

		DescribeLoadBalancersRequest lreq = new DescribeLoadBalancersRequest();
		lreq.withLoadBalancerNames("DemoA");

		DescribeLoadBalancersResult res = lb.describeLoadBalancers(lreq);

		com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription des = res
				.getLoadBalancerDescriptions().get(0);

		String ini = des.getDNSName();

		System.out.println("LoadBalancer DNS:" + ini);

		AmazonAutoScalingClient as = new AmazonAutoScalingClient(bawsc);

		// InstanceMonitoring mon = new InstanceMonitoring();

		// Boolean c = true;
		// mon.setEnabled(c);

		CreateLaunchConfigurationRequest lc = new CreateLaunchConfigurationRequest();

		lc.withImageId("ami-fab52b93");
		lc.withInstanceType("m1.small");
		lc.withLaunchConfigurationName("P3E");
		// lc.setInstanceMonitoring(mon);
		lc.setIamInstanceProfile("P3");
		lc.withSecurityGroups("launch-wizard-18");
		lc.withKeyName("vinoth");

		as.createLaunchConfiguration(lc);

		CreateAutoScalingGroupRequest gr = new CreateAutoScalingGroupRequest();

		gr.setMinSize(1);
		gr.setMaxSize(4);
		gr.setDesiredCapacity(1);
		gr.withLoadBalancerNames("DemoA");
		gr.withLaunchConfigurationName("P3E");
		gr.withAvailabilityZones("us-east-1d");
		gr.setAutoScalingGroupName("test-vin-P3E");

		as.createAutoScalingGroup(gr);

		DescribeAutoScalingGroupsRequest agr = new DescribeAutoScalingGroupsRequest();

		agr.getAutoScalingGroupNames();

		DescribeAutoScalingGroupsResult grpres = as
				.describeAutoScalingGroups(agr);

		System.out.println("Auto scaling grp:" + agr);

		PutScalingPolicyRequest pr = new PutScalingPolicyRequest();

		pr.withAutoScalingGroupName("test-vin-P3E");
		pr.setPolicyName("scaleout");
		pr.setAdjustmentType("ChangeInCapacity");
		pr.setScalingAdjustment(1);

		PutScalingPolicyResult pres = as.putScalingPolicy(pr);
		String arn = pres.getPolicyARN();

		AmazonCloudWatchClient cw = new AmazonCloudWatchClient(bawsc);

		Dimension dim = new Dimension();
		dim.setName("AutoScalingGroupName4");
		dim.setValue("test-vin-P3CE");
		List<Dimension> dimension = new ArrayList<Dimension>();
		dimension.add(dim);

		// AmazonCloudWatchClient cw = new AmazonCloudWatchClient(bawsc);

		Thread.sleep(10 * 60 * 1000);
		Process p1 = Runtime.getRuntime().exec(
				"mysql -u sysbench -pproject3 < status.sql ");
		System.out.println("output:" + p1);
		
		// p.waitFor();


		String line;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				p1.getInputStream()));
		while ((line = in.readLine()) != null) {
			System.out.println("output:" + line);
		}
		p1.waitFor();


		PutMetricDataRequest pd = new PutMetricDataRequest();
		MetricDatum md = new MetricDatum();
		md.setMetricName("TPS");
		md.setUnit(StandardUnit.Percent);
		md.setDimensions(dimension);
		pd.setNamespace("AWS/EC2");
		pd.withMetricData(md);

		PutMetricAlarmRequest ar = new PutMetricAlarmRequest();
		ar.setAlarmName("alarmv");
		ar.setMetricName("CPUUtilization");
		ar.setNamespace("AWS/EC2");
		ar.setStatistic("Average");
		ar.setPeriod(120);
		ar.setThreshold(75.0);
		ar.setUnit(StandardUnit.Percent);
		ar.setComparisonOperator("GreaterThanThreshold");
		ar.setEvaluationPeriods(1);
		@SuppressWarnings("rawtypes")
		List actions = new ArrayList();
		actions.add(arn);
		ar.setAlarmActions(actions);
		Dimension dim1 = new Dimension();
		dim.setName("AutoScalingGroupName4");
		dim.setValue("test-vin-P3CD");
		List<Dimension> dimension1 = new ArrayList<Dimension>();
		dimension1.add(dim1);

		ar.setDimensions(dimension1);

		cw.putMetricAlarm(ar);

	}

}
