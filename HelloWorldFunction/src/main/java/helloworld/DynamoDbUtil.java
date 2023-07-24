package helloworld;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DynamoDbUtil {

    // keyToGet: Key name (apiKey), key value (78a16add-fa3e-4921-904b-89dd867660b6)
    public static Map<String, AttributeValue> read(String tableName, Map<String, AttributeValue> keyToGet) {
        DynamoDbClient ddb = openDynamoClient();
        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();
        try {
            Map<String, AttributeValue> returnedItem = ddb.getItem(request).item();
            Set<String> keys = returnedItem.keySet();
            System.out.println("Amazon DynamoDB table attributes: \n");
            for (String key1 : keys) {
                System.out.println(String.format("Key: {%s}, Value: {%s}", key1, returnedItem.get(key1).toString()));
            }
            return returnedItem;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    // Saves the given params to the database
    public static void save(String tableName, HashMap<String, AttributeValue> itemValues) {
        DynamoDbClient ddb = openDynamoClient();
        try {
            PutItemRequest request = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(itemValues)
                    .build();
            try {
                PutItemResponse response = ddb.putItem(request);
                System.out.println(tableName +" was successfully updated. The request id is "+response.responseMetadata().requestId());
            } catch (ResourceNotFoundException e) {
                System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
                System.err.println("Be sure that it exists and that you've typed its name correctly!");
                throw e;
            } catch (DynamoDbException e) {
                System.err.println(e.getMessage());
                throw e;
            }
        } catch (Exception ex) {
            System.out.println("Exception while saving to DynamoDB. Exception is...");
            System.out.println(ex);
        } finally {
            ddb.close();
        }
    }

    private static DynamoDbClient openDynamoClient() {
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }
}
