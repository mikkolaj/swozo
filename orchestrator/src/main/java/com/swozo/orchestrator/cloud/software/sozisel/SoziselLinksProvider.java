package com.swozo.orchestrator.cloud.software.sozisel;

import com.swozo.model.users.ActivityRole;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


import java.io.IOException;

class SoziselLinksProvider {
    private static final String SOZISEL_PORT = "4000";
    private static final String JITSI_PORT = "8443";
    private static final String DEFAULT_MAIL = "awesome_mail@mock.com";
    private static final Integer MEETING_ESTIMATED_MINUTES = 90;
    private final String uri;
    private final String jitsiHost;
    private String bearerToken;
    private String roomId;

    SoziselLinksProvider(String hostAddress) {
        this.jitsiHost = "https://" + hostAddress + ":" + JITSI_PORT;
        this.uri = "http://" + hostAddress + ":" + SOZISEL_PORT + "/api/graphql";
    }

    String createLinks(String name, String surname, ActivityRole userRole) {
        bearerToken = "";
        sendRegister(DEFAULT_MAIL, name, surname);
        sendLogin(DEFAULT_MAIL);
        if (userRole == ActivityRole.TEACHER) {
            String sessionTemplateId = sendCreateSessionTemplate(MEETING_ESTIMATED_MINUTES);
            roomId = sendPlanSession(sessionTemplateId);
            sendStartSession(roomId);
        }
        String jitsiToken = sendGenerateJitsiToken(DEFAULT_MAIL, name + surname, roomId);

        return userRole == ActivityRole.TEACHER ?
                buildJitsiLink(roomId, jitsiToken, SoziselRequestTemplate.JITSI_TEACHER_LINK_PARAMETERS) :
                buildJitsiLink(roomId, jitsiToken, SoziselRequestTemplate.JITSI_STUDENT_LINK_PARAMETERS);
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

    private String sendGenerateJitsiToken(String mail, String fullName, String roomId) {
        String query = SoziselRequestTemplate.GENERATE_JITSI_TOKEN_QUERY;
        String variables = String.format(SoziselRequestTemplate.GENERATE_JITSI_TOKEN_VARIABLES, fullName, mail, roomId);
        JSONObject res = sendRequest(query, variables);
        return retrieveValueFromResponse(res, "generateJitsiToken", "token");
    }

    private JSONObject sendRequest(String query, String variables) {
        try {
            HttpResponse response = SoziselRequestSender.sendRequest(uri, query, variables, bearerToken);
            return parseResponseToJSON(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject parseResponseToJSON(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        return new JSONObject(EntityUtils.toString(entity, "UTF-8"));
    }

    private String retrieveValueFromResponse(JSONObject response, String operationName, String fieldName) {
        return response.getJSONObject("data").getJSONObject(operationName).getString(fieldName);
    }

    private String buildJitsiLink(String roomId, String jitsiToken, String parameters) {
        return jitsiHost +
                "/" +
                roomId +
                "?jwt=" +
                jitsiToken +
                parameters;
    }
}
