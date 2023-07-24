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
        try {
    //        DynamoDbClient ddb = openDynamoClient();
    //        read(ddb, tableName, apiKey, "");
            Map<String, AttributeValue> keyToGet = new HashMap<>();
            keyToGet.put("apiKey", AttributeValue.builder().s("abc1233").build());
            Map<String, AttributeValue> returnedItem = DynamoDbUtil.read(tableName, keyToGet);
            AttributeValue value = returnedItem.get("apiKey");
            boolean authenticated = false;
            if (value == null) {
                System.out.println("Value is null");
                authenticated = false;
            } else {
                System.out.println("value.s() is " + value.s());
                if (value.s().length() > 0) {
                    authenticated = true;
                } else {
                    authenticated = false;
                }
            }
    //        ddb.close();
            System.out.println("Authenticated is " + authenticated);
            return authenticated;
        } catch (Exception ex) {
            System.out.println("Exception while processing if user is authenticated or not");
            return false;
        }
    }

//    public void read(DynamoDbClient ddb,String tableName,String key,String keyVal ) {
//        HashMap<String,AttributeValue> keyToGet = new HashMap<String,AttributeValue>();
//        // Key name (apiKey), key value (78a16add-fa3e-4921-904b-89dd867660b6)
//        keyToGet.put("apiKey", AttributeValue.builder().s("abc1233").build());
//
//        GetItemRequest request = GetItemRequest.builder()
//                .key(keyToGet)
//                .tableName(tableName)
//                .build();
//
//        try {
//            Map<String, AttributeValue> returnedItem = ddb.getItem(request).item();
//            Set<String> keys = returnedItem.keySet();
//            System.out.println("Amazon DynamoDB table attributes: \n");
//            for (String key1 : keys) {
//                System.out.format("Key: {%s}, Value: {%s}", key1, returnedItem.get(key1).toString());
//            }
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//            throw e;
//        }
//    }
//
//    private DynamoDbClient openDynamoClient() {
//        return DynamoDbClient.builder()
//                .region(Region.US_EAST_1)
//                .build();
//    }

}
