package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.model.APIParam;
import org.example.model.ModelPlatform;
import org.example.model.ModelResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class APIService {
    private final HttpClient httpClient;
    private final Dotenv dotenv;

    private static APIService instance = new APIService();

    private APIService() {
        httpClient = HttpClient.newHttpClient();
        dotenv = Dotenv.load();
    }

    public static APIService getInstance() {
        if (instance == null) {
            instance = new APIService();
        }
        return instance;
    }

    public String callAPI(APIParam apiParam) throws Exception {
        String url = "";
        String token = "";

        switch (apiParam.model()) {
            case GROQ_LLAMA -> {
                url = "https://api.groq.com/openai/v1/chat/completions";
                token = dotenv.get("GROQ_KEY");
            }
            case TOGETHER_LLAMA -> {
                url = "https://api.together.xyz/v1/chat/completions";
                token = dotenv.get("TOGETHER_KEY");
            }
        }

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
                """.formatted(apiParam.prompt(), apiParam.model().name);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Authorization", "Bearer %s".formatted(token))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();
        ObjectMapper objectMapper = new ObjectMapper();
        ModelResponse modelResponse = objectMapper.readValue(responseBody, ModelResponse.class);
        return modelResponse.choices().get(0).message().content();
    }
}
