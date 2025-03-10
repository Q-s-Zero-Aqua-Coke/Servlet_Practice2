package org.example.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.model.APIParam;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIService {

    private static APIService instance = new APIService();

    private APIService() {
        httpClient = HttpClient.newHttpClient();
        dotenv = Dotenv.load();
    }

    private final HttpClient httpClient;
    private final Dotenv dotenv;

    public static APIService getInstance() {
        if (instance == null) {
            instance = new APIService();
        }
        return instance;
    }

    public String callAPI(APIParam apiParam) throws IOException, InterruptedException {
        String url = "https://api.groq.com/openai/v1/chat/completions";
        String token = dotenv.get("GROQ_KEY");
        String body = """
                {
                    "messages": [
                    {
                        "role": "system",
                        "content": "only korean character"
                    },
                    {
                        "role": "user",
                        "content": "%s"
                        }
                    ],
                        "model": "%s"
                }
                """.formatted(apiParam.prompt(), apiParam.model());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Authorization", "Bearer %s".formatted(token))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
