package com.swozo.orchestrator.cloud.software.sozisel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
class SoziselRequestSender {
    static HttpResponse sendRequest(String uri, String query, String variables, String bearerToken) throws IOException {
            Map<String, Object> requestPayload = Map.of("query", query, "variables", variables);
            StringEntity entity = new StringEntity(new JSONObject(requestPayload).toString());
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(uri);
            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "application/json");
            if (!bearerToken.equals(""))
                request.addHeader("Authorization", "Bearer " + bearerToken);
            request.setEntity(entity);
            return httpClient.execute(request);
    }
}
