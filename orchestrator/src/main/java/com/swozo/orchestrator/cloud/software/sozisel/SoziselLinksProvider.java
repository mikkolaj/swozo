package com.swozo.orchestrator.cloud.software.sozisel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONException;


import java.io.IOException;

class SoziselLinksProvider {
    private static final String SOZISEL_PORT = "4000";
    private static final String JITSI_PORT = "8443";
    private final String uri;
    private final String jitsiHost;
    private String bearerToken = "";

    SoziselLinksProvider(String hostAddress) {
        this.jitsiHost = "https://" + hostAddress + ":" + JITSI_PORT;
        this.uri = "http://" +  hostAddress + ":" + SOZISEL_PORT + "/api/graphql";
    }

    String createLinks() {
        String mail = "test@test.pl", name = "Imie", surname = "Nazwisko";
        Integer estimatedTime = 90;
        sendRegister(mail, name, surname);
        sendLogin(mail);
        String sessionTemplateId = sendCreateSessionTemplate(estimatedTime);
        String roomId = sendPlanSession(sessionTemplateId);
        sendStartSession(roomId);
        String jitsiToken = sendGenerateJitsiToken(mail, name, surname, roomId);

        return jitsiHost +
                "/" +
                roomId +
                "?jwt=" +
                jitsiToken +
                SoziselRequestTemplate.JITSI_LINK_PARAMETERS;
    }

    private void sendRegister(String mail, String name, String surname) {
        String query = SoziselRequestTemplate.REGISTER_QUERY;
        String variables = String.format(SoziselRequestTemplate.REGISTER_VARIABLES, mail, name, surname);
        sendRequest(query, variables);
    }

    private void sendLogin(String mail) {
        String query = SoziselRequestTemplate.LOGIN_QUERY;
        String variables = String.format(SoziselRequestTemplate.LOGIN_VARIABLES, mail);
        JSONObject res = sendRequest(query, variables);
        bearerToken = retrieveValueFromResponse(res, "login", "token");
    }

    private String sendCreateSessionTemplate(Integer estimatedTime) {
        String query = SoziselRequestTemplate.CREATE_SESSION_TEMPLATE_QUERY;
        String variables = String.format(SoziselRequestTemplate.CREATE_SESSION_TEMPLATE_VARIABLES, estimatedTime);
        JSONObject res = sendRequest(query, variables);
        return retrieveValueFromResponse(res, "createSessionTemplate", "id");
    }

    private String sendPlanSession(String sessionTemplateId) {
        String query = SoziselRequestTemplate.PLAN_SESSION_QUERY;
        String variables = String.format(SoziselRequestTemplate.PLAN_SESSION_VARIABLES, sessionTemplateId);
        JSONObject res = sendRequest(query, variables);
        return retrieveValueFromResponse(res, "createSession", "id");
    }

    private void sendStartSession(String roomId) {
        String query = SoziselRequestTemplate.START_SESSION_QUERY;
        String variables = String.format(SoziselRequestTemplate.START_SESSION_VARIABLES, roomId);
        sendRequest(query, variables);
    }

    private String sendGenerateJitsiToken(String mail, String name, String surname, String roomId) {
        String query = SoziselRequestTemplate.GENERATE_JITSI_TOKEN_QUERY;
        String variables = String.format(SoziselRequestTemplate.GENERATE_JITSI_TOKEN_VARIABLES, name + surname, mail, roomId);
        JSONObject res = sendRequest(query, variables);
        return retrieveValueFromResponse(res, "generateJitsiToken", "token");
    }

    private JSONObject sendRequest(String query, String variables) {
        try {
            HttpResponse response = SoziselRequestSender.sendRequest(uri, query, variables, bearerToken);
            JSONObject jsonObject = parseResponse(response);
            System.out.println(jsonObject);
            return jsonObject;
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject parseResponse(HttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        return new JSONObject(EntityUtils.toString(entity, "UTF-8"));
    }

    private String retrieveValueFromResponse(JSONObject response, String operationName, String fieldName) {
        try {
            return response.getJSONObject("data").getJSONObject(operationName).getString(fieldName);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}