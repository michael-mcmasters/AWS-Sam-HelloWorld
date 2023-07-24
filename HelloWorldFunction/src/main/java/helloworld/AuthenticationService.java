package helloworld;

import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AuthenticationService {

    private String tableName;

    public AuthenticationService() {
        this.tableName = System.getenv("AUTHENTICATION_TABLE");
        System.out.println("tableName is " + tableName);
    }

    public boolean authenticateApiKey(String apiKey) {
//        read(apiKey);
        DynamoDbClient ddb = openDynamoClient();
        read(ddb, tableName, apiKey, "");
        ddb.close();
        return true;
    }

    public void read(DynamoDbClient ddb,String tableName,String key,String keyVal ) {

        HashMap<String,AttributeValue> keyToGet = new HashMap<String,AttributeValue>();

        keyToGet.put("apiKey", AttributeValue.builder()
                .s("abc1233").build());

//        keyToGet.put(key, AttributeValue.builder().build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();

        try {
            Map<String,AttributeValue> returnedItem = ddb.getItem(request).item();

            if (returnedItem != null) {
                Set<String> keys = returnedItem.keySet();
                System.out.println("Amazon DynamoDB table attributes: \n");

                for (String key1 : keys) {
                    System.out.format("%s: %s\n", key1, returnedItem.get(key1).toString());
                }
            } else {
                System.out.format("No item found with the key %s!\n", key);
            }
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        }
    }

//    private void read(String apiKey) {
//        DynamoDbClient ddb = openDynamoClient();
//        try {
//            //
////            ddb.getItem(apiKey);
//
////            PutItemRequest request = PutItemRequest.builder()
////                    .tableName(tableName)
////                    .item(itemValues)
////                    .build();
//
//            HashMap<String,AttributeValue> keyToGet = new HashMap<String,AttributeValue>();
//
//            keyToGet.put(key, AttributeValue.builder()
//                    .s(keyVal).build());
//
//
//            Map<String, AttributeValue> keysMap = new HashMap<>();
//            keysMap.put("apiKey", new AttributeValue("a"));
//
//            GetItemRequest request = GetItemRequest.builder()
//                    .tableName(tableName)
//                    .key(keysMap)
//                    .build();
//
//            ddb.getItem(request);
//
//
//        } catch (Exception ex) {
//            System.out.println("Exception while saving to DynamoDB. Exception is...");
//            System.out.println(ex);
//        } finally {
//            ddb.close();
//        }
//    }

    private DynamoDbClient openDynamoClient() {
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

}
