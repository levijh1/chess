import server.request.RegisterRequest;
import server.response.RegisterAndLoginResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;

import com.google.gson.Gson;


public class ClientCommunicator {
    public String doGet(String urlString, String authToken) throws IOException {
        InputStream responseBody;
        //only used for get games
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");

        connection.addRequestProperty("Authorization", authToken);

        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            responseBody = connection.getInputStream();
        } else {
            //Server returned an http error
            responseBody = connection.getErrorStream();
        }
        return responseBody.toString();
    }

    public String doPost(String urlString, RegisterRequest request, String authToken) throws IOException {
        URL url = new URL(urlString);
        InputStream responseBody;

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        if (authToken != null) {
            connection.addRequestProperty("Authorization", authToken);
        }

        connection.connect();

        try (OutputStream requestBody = connection.getOutputStream();) {
            String jsonRequest = new Gson().toJson(request);
            requestBody.write(jsonRequest.getBytes());
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            //Get response headers

            responseBody = connection.getInputStream();
            //TODO: I think the request is sent just fine, but I'm not sure where we are getting a response like this. How do we get the response we want? We aren't getting a JSON object back of the shape we expect
            RegisterAndLoginResponse response = new Gson().fromJson(responseBody.toString(), RegisterAndLoginResponse.class);
            return response.toString();
        } else {
            //server returned an HTTP ERROR

            responseBody = connection.getErrorStream();
            return responseBody.toString();

        }
    }
}
