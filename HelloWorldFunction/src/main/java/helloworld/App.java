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

    private static final String version = "26";

    private AuthenticationService authenticationService;

    private SamJavaTableService samJavaTableService;


    public App() {
        this.authenticationService = new AuthenticationService();
        this.samJavaTableService = new SamJavaTableService();
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        try {
            Log.info("Lambda received request for API version " + version);

            String apiKey = input.getHeaders().get("auth-token");
            Log.info("API key is " + apiKey);
            if (!authenticationService.userIsAuthorized(apiKey)) {
                throw new Exception("Authentication failed for API");
            }

            samJavaTableService.save("id", version);
//          samJavaTableService.save("version", version);
            Log.info("Completed saving to DynamoDb");

            String body = String.format("{ \"message\": \"hello world\", \"version\": \"%s\" }", version);
            return generateResponse(true, body);
        } catch (Exception ex) {
            Log.info("Exception in handler", ex);
            return generateResponse(false, "{}");
        } finally {
            Log.info("Completed processing request");
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
            return response.withStatusCode(200).withBody(body);
        } else {
            return response.withBody(body).withStatusCode(500);
        }
    }
}
