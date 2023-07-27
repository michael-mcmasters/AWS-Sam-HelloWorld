package helloworld;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {

    private final String tableName;

    private final String tableKey;


    public AuthenticationService() {
        this.tableName = System.getenv("AUTHENTICATION_TABLE");
        this.tableKey = "apiKey";
        Log.info("tableName is " + tableName);
    }

    public boolean userIsAuthorized(String apiKeyValue) {
        try {
            Log.info("Processing if user is authorized");
            if (apiKeyValue == null) {
                Log.info("apiKeyValue is null. User is not authorized");
                return false;
            }

            Map<String, AttributeValue> keyToGet = new HashMap<>();
            keyToGet.put(tableKey, AttributeValue.builder().s(apiKeyValue).build());
            Map<String, AttributeValue> returnedItem = DynamoDbUtil.read(tableName, keyToGet);
            AttributeValue value = returnedItem.get("apiKey");
            boolean authenticated = false;
            if (value == null) {
                Log.info("Value is null");
                authenticated = false;
            } else {
                Log.info("value.s() is " + value.s());
                if (value.s().length() > 0) {
                    authenticated = true;
                } else {
                    authenticated = false;
                }
            }
            Log.info("Finished processing if user is authenticated. Authenticated: " + authenticated);
            return authenticated;
        } catch (Exception ex) {
            Log.info("Exception while processing if user is authenticated", ex);
            return false;
        }
    }

}
