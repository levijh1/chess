import server.response.ErrorResponse;
import server.response.ParentResponse;
import server.response.RegisterAndLoginResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Objects;
import java.util.Scanner;

import com.google.gson.Gson;


public class ClientCommunicator {
    public <T> ParentResponse sendRequest(String requestMethod, String urlString, T request, Class<? extends ParentResponse> responseClass, String authToken) throws IOException {
        URL url = new URL(urlString);
        InputStream responseBody;

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

//        connection.setReadTimeout(5000);
        connection.setRequestMethod(requestMethod);

        if (!Objects.equals(requestMethod, "GET")) {
            connection.setDoOutput(true);
        }

        if (authToken != null) {
            connection.addRequestProperty("Authorization", authToken);
        }

        connection.connect();

        if (!Objects.equals(requestMethod, "GET")) {
            try (OutputStream requestBody = connection.getOutputStream();) {
                String jsonRequest = new Gson().toJson(request);
                requestBody.write(jsonRequest.getBytes());
            }
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            //Get response headers

            responseBody = connection.getInputStream();
            String responseString = convertStreamToString(responseBody);
            return new Gson().fromJson(responseString, responseClass);
        } else {
            //server returned an HTTP ERROR
            responseBody = connection.getErrorStream();
            String responseString = convertStreamToString(responseBody);
            return new Gson().fromJson(responseString, ErrorResponse.class);
        }

    }

    public static String convertStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
