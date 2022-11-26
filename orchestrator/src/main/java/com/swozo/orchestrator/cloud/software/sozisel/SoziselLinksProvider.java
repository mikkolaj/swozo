package com.swozo.orchestrator.cloud.software.sozisel;

import com.swozo.model.users.ActivityRole;
import lombok.SneakyThrows;
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
    private static final int RETRY_WAITING_MILLISECONDS = 5000;
    private static final int MAX_RETRIES = 3;
    private final String uri;
    private final String jitsiHost;
    private String bearerToken;
    private String roomId;
    private int retries = 0;

    SoziselLinksProvider(String hostAddress) {
        this.jitsiHost = "https://" + hostAddress + ":" + JITSI_PORT;
        this.uri = "http://" + hostAddress + ":" + SOZISEL_PORT + "/api/graphql";
    }

    @SneakyThrows
    String createLinks(String name, String surname, ActivityRole userRole) {
        try {
            return sendRequestsAndBuildLink(name, surname, userRole);
        } catch (IOException err) {
            if (retries > MAX_RETRIES) {
                throw err;
            }
            retries++;
            Thread.sleep(RETRY_WAITING_MILLISECONDS);
            return createLinks(name, surname, userRole);
        }
    }

    private String sendRequestsAndBuildLink(String name, String surname, ActivityRole userRole) throws IOException {
        bearerToken = "";
        sendRegister(DEFAULT_MAIL, name, surname);
        sendLogin(DEFAULT_MAIL);
        if (userRole == ActivityRole.TEACHER) {
            String sessionTemplateId = sendCreateSessionTemplate(MEETING_ESTIMATED_MINUTES);
            roomId = sendPlanSession(sessionTemplateId);
            sendStartSession(roomId);
        }
        String jitsiToken = sendGenerateJitsiToken(DEFAULT_MAIL, name + " " + surname, roomId);

        return buildJitsiLink(roomId, jitsiToken, userRole == ActivityRole.TEACHER ?
                SoziselRequestTemplate.PARAMETERS.TEACHER_LINK :
                SoziselRequestTemplate.PARAMETERS.STUDENT_LINK);
    }

    private void sendRegister(String mail, String name, String surname) throws IOException {
        String query = SoziselRequestTemplate.QUERY.REGISTER;
        String variables = String.format(SoziselRequestTemplate.VARIABLES.REGISTER, mail, name, surname);
        sendRequest(query, variables);
    }

    private void sendLogin(String mail) throws IOException {
        String query = SoziselRequestTemplate.QUERY.LOGIN;
        String variables = String.format(SoziselRequestTemplate.VARIABLES.LOGIN, mail);
        JSONObject res = sendRequest(query, variables);
        bearerToken = retrieveValueFromResponse(res, "login", "token");
    }

    private String sendCreateSessionTemplate(Integer estimatedTime) throws IOException {
        String query = SoziselRequestTemplate.QUERY.CREATE_SESSION_TEMPLATE;
        String variables = String.format(SoziselRequestTemplate.VARIABLES.CREATE_SESSION_TEMPLATE, estimatedTime);
        JSONObject res = sendRequest(query, variables);
        return retrieveValueFromResponse(res, "createSessionTemplate", "id");
    }

    private String sendPlanSession(String sessionTemplateId) throws IOException {
        String query = SoziselRequestTemplate.QUERY.PLAN_SESSION;
        String variables = String.format(SoziselRequestTemplate.VARIABLES.PLAN_SESSION, sessionTemplateId);
        JSONObject res = sendRequest(query, variables);
        return retrieveValueFromResponse(res, "createSession", "id");
    }

    private void sendStartSession(String roomId) throws IOException {
        String query = SoziselRequestTemplate.QUERY.START_SESSION;
        String variables = String.format(SoziselRequestTemplate.VARIABLES.START_SESSION, roomId);
        sendRequest(query, variables);
    }

    private String sendGenerateJitsiToken(String mail, String fullName, String roomId) throws IOException {
        String query = SoziselRequestTemplate.QUERY.GENERATE_JITSI_TOKEN;
        String variables = String.format(SoziselRequestTemplate.VARIABLES.GENERATE_JITSI_TOKEN, fullName, mail, roomId);
        JSONObject res = sendRequest(query, variables);
        return retrieveValueFromResponse(res, "generateJitsiToken", "token");
    }

    private JSONObject sendRequest(String query, String variables) throws IOException {
        HttpResponse response = SoziselRequestSender.sendRequest(uri, query, variables, bearerToken);
        return parseResponseToJSON(response);
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
