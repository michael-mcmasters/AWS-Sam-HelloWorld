package helloworld;

import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;

public class samJavaTableService {

    private String tableName;

    public samJavaTableService() {
        this.tableName = System.getenv("TABLE_NAME");
        System.out.println("tableName is " + tableName);
    }

    public void save(String key, String value) {
        try {
            System.out.println("Saving key/value");
            HashMap<String, AttributeValue> itemValues = new HashMap<>();
            itemValues.put(key, AttributeValue.builder().s(value).build());
            DynamoDbUtil.write(tableName, itemValues);
            System.out.println("Finished saving key/value");
        } catch (Exception ex) {
            System.out.println("Exception while saving key/value. Exception is...");
            System.out.println(ex);
            throw ex;
        }
    }

}
