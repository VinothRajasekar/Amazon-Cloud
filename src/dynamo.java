import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.util.HashMap;

import java.util.Map;



import com.amazonaws.AmazonClientException;

import com.amazonaws.AmazonServiceException;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;

import com.amazonaws.auth.PropertiesCredentials;

import com.amazonaws.regions.Region;

import com.amazonaws.regions.Regions;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

import com.amazonaws.services.dynamodbv2.model.PutItemResult;

import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.AmazonS3Client;

import com.amazonaws.services.s3.model.GetObjectRequest;

import com.amazonaws.services.s3.model.S3Object;





public class dynamo {

    static AmazonDynamoDBClient dynamoDB;

    public static String tableName;



    private static void init() throws Exception {

    /*

* To load the credentials provider implementation from a properties file at the root of your class path

*/

        dynamoDB = new AmazonDynamoDBClient(new ClasspathPropertiesFileCredentialsProvider());

        Region usEast1 = Region.getRegion(Regions.US_EAST_1);

        dynamoDB.setRegion(usEast1);

    }



public static void main(String[] args) throws Exception {

AmazonS3 s3 = new AmazonS3Client(new PropertiesCredentials(

dynamo.class.getResourceAsStream("AwsCredentials.properties")));

init();



        try {

            tableName = "Caltech";

            String bucketName = "vintab";

            String key = "caltech-256.csv";

            

            System.out.println("Downloading an object");

            S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));

            System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());

            pushInDynamDB(object.getObjectContent());

    

        } catch (AmazonServiceException ase) {

            System.out.println("Caught an AmazonServiceException, which means your request made it "

                    + "to AWS, but was rejected with an error response for some reason.");

            System.out.println("Error Message:    " + ase.getMessage());

            System.out.println("HTTP Status Code: " + ase.getStatusCode());

            System.out.println("AWS Error Code:   " + ase.getErrorCode());

            System.out.println("Error Type:       " + ase.getErrorType());

            System.out.println("Request ID:       " + ase.getRequestId());

        } catch (AmazonClientException ace) {

            System.out.println("Caught an AmazonClientException, which means the client encountered "

                    + "a serious internal problem while trying to communicate with AWS, "

                    + "such as not being able to access the network.");

            System.out.println("Error Message: " + ace.getMessage());

        }

    }



private static void pushInDynamDB(InputStream input) throws IOException {

String cvsSplitBy = ",";

int count=0;

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        while (true) {

            String line = reader.readLine();

            if (line == null) break;

            String[] item = line.split(cvsSplitBy);

            if(count!=0){

            Map<String, AttributeValue> newitem = newItem(item[0], Integer.parseInt(item[1]),item[2]);

            

//        Add an item

          PutItemRequest putItemRequest = new PutItemRequest(tableName, newitem);

          PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);

//          System.out.println("Result: " + putItemResult);

          



            System.out.println(line);

            count++;

            }

            else if (count==0) count++;   

        }

        System.out.println();

    }


private static Map<String, AttributeValue> newItem(String Category, int Picture, String s3url) {

        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();

        item.put("Category", new AttributeValue(Category));

        item.put("Picture", new AttributeValue().withN(Integer.toString(Picture)));

        item.put("s3url", new AttributeValue(s3url));

        return item;

    }

}