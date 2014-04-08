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


public class AutoScaling {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, InterruptedException {
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
		
		InstanceMonitoring mon = new InstanceMonitoring();
		
		Boolean c = true;
		mon.setEnabled(c);

		 CreateLaunchConfigurationRequest lc = new CreateLaunchConfigurationRequest();
		  
		   
		
		  lc.withImageId("ami-2b7b2c42");
		  lc.withInstanceType("m1.small");
		  lc.withLaunchConfigurationName("Demo15");
		  lc.setInstanceMonitoring(mon);
	      
		  System.out.println("moni:" + lc.getInstanceMonitoring());
		  as.createLaunchConfiguration(lc); 
		  
		   
		  CreateAutoScalingGroupRequest gr = new CreateAutoScalingGroupRequest();
		  

		
		  gr.setMinSize(2);
		  gr.setMaxSize(5);
		  gr.setDesiredCapacity(2);
		  gr.withLoadBalancerNames("DemoA");
		  gr.withLaunchConfigurationName("Demo15");
		  gr.withAvailabilityZones("us-east-1d");
		  gr.setAutoScalingGroupName("test-vin");
		  
		  
		 as.createAutoScalingGroup(gr);
		
		  
		  DescribeAutoScalingGroupsRequest agr = new DescribeAutoScalingGroupsRequest();
		 
		  agr.getAutoScalingGroupNames();
		 
		  DescribeAutoScalingGroupsResult grpres = as.describeAutoScalingGroups(agr);
		  
		  System.out.println("Auto scaling grp:" + agr);
		 
		  PutScalingPolicyRequest pr = new PutScalingPolicyRequest();
		  

		  pr.withAutoScalingGroupName("test-vin");
		  pr.setPolicyName("scaleout");
		  pr.setAdjustmentType("ChangeInCapacity");
		  pr.setScalingAdjustment(1);
		  
		  PutScalingPolicyResult pres = as.putScalingPolicy(pr); 
		  
		  String arn = pres.getPolicyARN();
		  System.out.println("policyname:" + arn);
		  
		  PutScalingPolicyRequest pr1 = new PutScalingPolicyRequest();
		
		  pr1.withAutoScalingGroupName("test-vin");
		  pr1.setPolicyName("scalein");
		  pr1.setAdjustmentType("ChangeInCapacity");
		  pr1.setScalingAdjustment(-1);
		  
		  
		  PutScalingPolicyResult pres1 = as.putScalingPolicy(pr1); 
		 
		  String arn1 = pres1.getPolicyARN();
		 
		 AmazonCloudWatchClient cw = new AmazonCloudWatchClient(bawsc);

		  
		  PutNotificationConfigurationRequest creq = new PutNotificationConfigurationRequest();

		 
		 List<String> notify = new ArrayList<String>();
		  notify.add("autoscaling:EC2_INSTANCE_LAUNCH");
		  notify.add("autoscaling:EC2_INSTANCE_TERMINATE");
		 
		  creq.withAutoScalingGroupName("test-vin");
		  creq.setTopicARN("arn:aws:sns:us-east-1:951227464532:CMU_VIN");
		  creq.setNotificationTypes(notify);
		 
		  as.putNotificationConfiguration(creq);
		  
		  PutMetricDataRequest md = new PutMetricDataRequest();
		  MetricDatum md1 = new MetricDatum();
		 
		 
		 PutMetricAlarmRequest ar = new PutMetricAlarmRequest();
		 ar.setAlarmName("alarmv");
		 ar.setMetricName("CPUUtilization");
		 ar.setNamespace("AWS/EC2");
		 ar.setStatistic("Average");
		 ar.setPeriod(300);
		 ar.setThreshold(80.0);
		 ar.setUnit(StandardUnit.Percent);
		 ar.setComparisonOperator("GreaterThanThreshold");
		 ar.setEvaluationPeriods(1);
		 @SuppressWarnings("rawtypes")
		List actions = new ArrayList();
		 actions.add(arn);
		 ar.setAlarmActions(actions);
	       // List dimensions = new ArrayList();
		    Dimension dim = new Dimension();
		    dim.setName("AutoScalingGroupName");
		    dim.setValue("test-vin");
	        List<Dimension> dimension = new ArrayList<Dimension>();
	        dimension.add(dim);
	        

	        ar.setDimensions(dimension);
	        
		 cw.putMetricAlarm(ar);
		 
	 PutMetricAlarmRequest ar1 = new PutMetricAlarmRequest();
		 
		 ar1.setAlarmName("alarmh");
		 ar1.setMetricName("CPUUtilization");
		 ar1.setNamespace("AWS/EC2");
		 ar1.setStatistic("Average");
		 ar1.setPeriod(300);
		 ar1.setThreshold(20.0);
		 ar1.setUnit(StandardUnit.Percent);
		 ar1.setComparisonOperator("LessThanThreshold");
		 ar1.setEvaluationPeriods(1);
		  @SuppressWarnings("rawtypes")
		List actions1 = new ArrayList();
		  actions1.add(arn1);

		 ar1.setAlarmActions(actions1);

	        ar1.setDimensions(dimension);
	        
		 cw.putMetricAlarm(ar1);
		 
		    Thread.sleep(8 * 60 * 1000);
		    
			Process p = Runtime.getRuntime().exec(
				"/home/ubuntu/benchmark/seige_bench.sh "
						+ ini + " logfile");
			//p.waitFor();

			 System.out.println("process launched:" + p);
			
			String line;
			BufferedReader in = new BufferedReader(
	                   new InputStreamReader(p.getInputStream()) );
	           while ((line = in.readLine()) != null) {
	             System.out.println("output:" + line);
	           }
			   p.waitFor();

	}

}
