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
            Log.info("Amazon DynamoDB table attributes: \n");
            for (String key1 : keys) {
                Log.info(String.format("Key: {%s}, Value: {%s}", key1, returnedItem.get(key1).toString()));
            }
            return returnedItem;
        } catch (Exception e) {
            Log.info("Exception reading from database", e);
            throw e;
        } finally {
            ddb.close();
        }
    }

    // Saves the given params to the database
    public static void write(String tableName, HashMap<String, AttributeValue> itemValues) {
        DynamoDbClient ddb = openDynamoClient();
        try {
            PutItemRequest request = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(itemValues)
                    .build();
            try {
                PutItemResponse response = ddb.putItem(request);
                Log.info(tableName + " was successfully updated. The request id is " +response.responseMetadata().requestId());
            } catch (ResourceNotFoundException e) {
                Log.info(String.format("Error: The Amazon DynamoDB table \"{}\" can't be found.\n", tableName));
                throw e;
            } catch (DynamoDbException e) {
                Log.info("Exception writing to database", e);
                throw e;
            }
        } catch (Exception ex) {
            Log.info("Exception while saving to DynamoDB.", ex);
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
