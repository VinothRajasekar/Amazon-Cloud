import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;

public class Dynamo1 {

	/**
	 * @param args
	 */

	static AmazonDynamoDBClient client;

	static String TableName = "CalTech256";

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		createClient();

		try {
			uploadSampleProducts(TableName);
		} catch (AmazonServiceException ase) {
			System.err.println("Data load script failed.");
		}
	}

	private static void createClient() throws IOException {
		// TODO Auto-generated method stub
		AWSCredentials credentials = new PropertiesCredentials(
				Dynamo1.class.getResourceAsStream("AwsCredentials.properties"));
		client = new AmazonDynamoDBClient(credentials);
	}

	private static void uploadSampleProducts(String tableName2)
			throws IOException {
		// TODO Auto-generated method stub

		String csvFile = "/Users/Vinoth/Downloads/caltech-256.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		System.out.println("Uploading Data in the table " + TableName);

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] items = line.split(cvsSplitBy);
				System.out.println(line);
				Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
				item.put("Category", new AttributeValue(items[0]));
				item.put("Picture", new AttributeValue().withN(items[1]));
				item.put("S3URL", new AttributeValue(items[2]));

				PutItemRequest itemRequest = new PutItemRequest(TableName, item);

				@SuppressWarnings("unused")
				PutItemResult putItemResult = client.putItem(itemRequest);
				item.clear();
				System.out.println(line);

			}

		} catch (AmazonServiceException ase) {
			System.err.println("Failed to create item in " + TableName + " "
					+ ase);
		}
		System.out.println("Data Upload Complete for the table " + TableName);

	}
	
	 
	
}