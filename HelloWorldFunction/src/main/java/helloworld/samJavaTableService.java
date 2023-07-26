package helloworld;

import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;

public class samJavaTableService {

//    private Logger log = LoggerFactory.getLogger(samJavaTableService.class);

    private String tableName;


    public samJavaTableService() {
        this.tableName = System.getenv("TABLE_NAME");
        Log.info("tableName is " + tableName);
    }

    public void save(String key, String value) {
        try {
            Log.info("Saving key/value");
            HashMap<String, AttributeValue> itemValues = new HashMap<>();
            itemValues.put(key, AttributeValue.builder().s(value).build());
            DynamoDbUtil.write(tableName, itemValues);
            Log.info("Finished saving key/value");
        } catch (Exception ex) {
            Log.info("Exception while saving key/value.", ex);
            throw ex;
        }
    }

}
