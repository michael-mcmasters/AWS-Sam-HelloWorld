package helloworld;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private AuthenticationService authenticationService;
    private DynamoDbService dynamoDbService;


    public App() {
        this.authenticationService = new AuthenticationService();
        this.dynamoDbService = new DynamoDbService();
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        try {
            String version = "15";
            System.out.println("Version " + version);

            String apiKey = input.getHeaders().get("auth-token");
            System.out.println("API key is " + apiKey);
            if (apiKey == null || !authenticationService.authenticateApiKey(apiKey)) {
                throw new Exception("Authentication failed for API");
            }

            dynamoDbService.saveExample();
            System.out.println("Completed saving to DynamoDb");

            String body = String.format("{ \"message\": \"hello world\", \"version\": \"%s\" }", version);
            return generateResponse(true, body);
        } catch (Exception ex) {
            System.out.println("Exception in handler. Exception is...");
            System.out.println(ex);
            return generateResponse(false, "{}");
        } finally {
            System.out.println("Completed request");
        }
    }

    private APIGatewayProxyResponseEvent generateResponse(boolean successful, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD");
        headers.put("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        headers.put("Access-Control-Allow-Credentials", "true");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        if (successful) {
            return response
                    .withStatusCode(200)
                    .withBody(body);
        } else {
            return response
                    .withBody(body)
                    .withStatusCode(500);
        }
    }

//    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
//        String version = "7";
//        System.out.println("Version " + version);
//
//        try {
//            dynamoDbService.saveExample();
//            System.out.println("Saved successfully!");
//        } catch (Exception ex) {
//            System.out.println("Exception writing to DynamoDb. Exception is ...");
//            System.out.println(ex);
//        }
//
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json");
//        headers.put("X-Custom-Header", "application/json");
//
//        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
//                .withHeaders(headers);
//        try {
//            String output = String.format("{ \"message\": \"hello world\", \"version\": \"%s\" }", version);
//
//            return response
//                    .withStatusCode(200)
//                    .withBody(output);
//        } catch (Exception e) {
//            return response
//                    .withBody("{}")
//                    .withStatusCode(500);
//        }
//    }
}
