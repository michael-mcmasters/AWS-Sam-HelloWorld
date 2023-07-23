package helloworld;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;

public class DynamoDbService {

    private String tableName;

    public DynamoDbService() {
        this.tableName = System.getenv("TABLE_NAME");
        System.out.println("tableName is " + tableName);
    }

    // Saves a quick example to the database
    public void saveExample() {
        save(
            "id",
            "pie7",
            "albumTitle",
            "albumTitleValue",
            "awards",
            "awardVal",
            "songTitle",
            "songTitleVal"
        );
    }

    // Saves the given params to the database
    public void save(String key, String keyVal, String albumTitle, String albumTitleValue, String awards, String awardVal, String songTitle, String songTitleVal) {
        DynamoDbClient ddb = openDynamoClient();
        try {
            HashMap<String, AttributeValue> itemValues = new HashMap<>();
            itemValues.put(key, AttributeValue.builder().s(keyVal).build());
            itemValues.put(songTitle, AttributeValue.builder().s(songTitleVal).build());
            itemValues.put(albumTitle, AttributeValue.builder().s(albumTitleValue).build());
            itemValues.put(awards, AttributeValue.builder().s(awardVal).build());

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
                System.exit(1);
            } catch (DynamoDbException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        } catch (Exception ex) {
            System.out.println("Exception while saving to DynamoDB. Exception is...");
            System.out.println(ex);
        } finally {
            ddb.close();
        }
    }

    private DynamoDbClient openDynamoClient() {
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

}
