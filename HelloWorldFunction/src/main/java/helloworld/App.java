package helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        System.out.println("Version 4");

        //        String tableName = System.getenv("TABLE_NAME");
//        System.out.println("tableName is " + tableName);
//        try {
//
//            AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
//            DynamoDB dynamoDB = new DynamoDB(client);
//            Table table = dynamoDB.getTable("sam-java-table");
//            System.out.println("table is " + table);
//
//            Item item = new Item()
//                    .withPrimaryKey("Id", 123)
//                    .withString("Title", "Bicycle 123")
//                    .withString("Description", "123 description")
//                    .withString("BicycleType", "Hybrid")
//                    .withString("Brand", "Brand-Company C")
//                    .withNumber("Price", 500)
//                    .withStringSet("Color",  new HashSet<String>(Arrays.asList("Red", "Black")))
//                    .withString("ProductCategory", "Bicycle")
//                    .withBoolean("InStock", true)
//                    .withNull("QuantityOnHand");
//
//            // Write the item to the table
//            PutItemOutcome outcome = table.putItem(item);
//            System.out.println("outcome is " + outcome);
//        } catch (Exception ex) {
//            System.out.println("Exception writing to DynamoDb. Exception is ...");
//            System.out.println(ex);
//        }


        final String usage = "\n" +
                "Usage:\n" +
                "    <tableName> <key> <keyVal> <albumtitle> <albumtitleval> <awards> <awardsval> <Songtitle> <songtitleval>\n\n" +
                "Where:\n" +
                "    tableName - The Amazon DynamoDB table in which an item is placed (for example, Music3).\n" +
                "    key - The key used in the Amazon DynamoDB table (for example, Artist).\n" +
                "    keyval - The key value that represents the item to get (for example, Famous Band).\n" +
                "    albumTitle - The Album title (for example, AlbumTitle).\n" +
                "    AlbumTitleValue - The name of the album (for example, Songs About Life ).\n" +
                "    Awards - The awards column (for example, Awards).\n" +
                "    AwardVal - The value of the awards (for example, 10).\n" +
                "    SongTitle - The song title (for example, SongTitle).\n" +
                "    SongTitleVal - The value of the song title (for example, Happy Day).\n" +
                "**Warning** This program will  place an item that you specify into a table!\n";

        String[] args = new String[9];
        if (args.length != 9) {
            System.out.println(usage);
            System.exit(1);
        }

        String tableName = "sam-java-table";
        String key = "id";
        String keyVal = "pie";
        String albumTitle = "albumTitle";
        String albumTitleValue = "albumTitleValue";
        String awards = "awards";
        String awardVal = "awardVal";
        String songTitle = "songTitle";
        String songTitleVal = "songTitleVal";


//        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
//                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();

        putItemInTable(ddb, tableName, key, keyVal, albumTitle, albumTitleValue, awards, awardVal, songTitle, songTitleVal);
        System.out.println("Done!");
        ddb.close();





        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        try {
            final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
            String output = String.format("{ \"message\": \"hello world\", \"location\": \"%s\" }", pageContents);

            return response
                    .withStatusCode(200)
                    .withBody(output);
        } catch (IOException e) {
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
    }

    public static void putItemInTable(DynamoDbClient ddb,
                                      String tableName,
                                      String key,
                                      String keyVal,
                                      String albumTitle,
                                      String albumTitleValue,
                                      String awards,
                                      String awardVal,
                                      String songTitle,
                                      String songTitleVal){

        HashMap<String,AttributeValue> itemValues = new HashMap<>();
        itemValues.put(key, AttributeValue.builder().s(keyVal).build());
//        itemValues.put(songTitle, AttributeValue.builder().s(songTitleVal).build());
//        itemValues.put(albumTitle, AttributeValue.builder().s(albumTitleValue).build());
//        itemValues.put(awards, AttributeValue.builder().s(awardVal).build());

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
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
