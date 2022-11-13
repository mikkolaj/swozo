package com.swozo.orchestrator.cloud.software.sozisel;

class SoziselRequestTemplate {
    final static String REGISTER_QUERY = "mutation Register($input: RegisterInput!) {\n" +
            "    register(input: $input) {\n" +
            "        id\n" +
            "        __typename\n" +
            "        }\n" +
            "}";

    final static String LOGIN_QUERY = "mutation Login($input: LoginInput!) {\n" +
            "    login(input: $input) {\n" +
            "        token\n" +
            "        __typename\n" +
            "    }\n" +
            "}";

    final static String CREATE_SESSION_TEMPLATE_QUERY = "mutation CreateSessionTemplate($input: CreateSessionTemplateInput!) {\n" +
            "    createSessionTemplate(input: $input) {\n" +
            "        id\n" +
            "        __typename\n" +
            "    }\n" +
            "}";

    final static String PLAN_SESSION_QUERY = "mutation CreateSession($input: CreateSessionInput!) {\n" +
            "    createSession(input: $input) {\n" +
            "        id\n" +
            "        __typename\n" +
            "    }\n" +
            "}";

    final static String START_SESSION_QUERY = "mutation StartSession($id: ID!) {\n" +
            "    startSession(id: $id) {\n" +
            "        id\n" +
            "        __typename\n" +
            "    }\n" +
            "}";

    final static String GENERATE_JITSI_TOKEN_QUERY = "query GenerateJitsiToken($displayName: String!, $email: String!, $roomId: ID!) {\n" +
            "    generateJitsiToken(displayName: $displayName, email: $email, roomId: $roomId) {\n" +
            "        token\n" +
            "        email\n" +
            "        displayName\n" +
            "        __typename\n" +
            "    }\n" +
            "}";

    final static String REGISTER_VARIABLES = "{\n" +
            "    \"input\": {\n" +
            "      \"email\": \"%s\",\n" +
            "      \"firstName\": \"%s\",\n" +
            "      \"lastName\": \"%s\",\n" +
            "      \"password\": \"SuperSecretPassword\"\n" +
            "    }\n" +
            "}";

    final static String LOGIN_VARIABLES = "{\n" +
            "    \"input\": {\n" +
            "      \"email\": \"%s\",\n" +
            "      \"password\": \"SuperSecretPassword\"\n" +
            "    }\n" +
            "}";

    final static String CREATE_SESSION_TEMPLATE_VARIABLES = "{\n" +
            "    \"input\": {\n" +
            "      \"isPublic\": false,\n" +
            "      \"estimatedTime\": %d,\n" +
            "      \"agendaEntries\": [],\n" +
            "      \"name\": \"Jitsi meeting\"\n" +
            "    }\n" +
            "}";

    final static String PLAN_SESSION_VARIABLES = "{\n" +
            "    \"input\": {\n" +
            "      \"entryPassword\": \"hello_im_a_password\",\n" +
            "      \"name\": \"Session name\",\n" +
            "      \"scheduledStartTime\": \"2022-10-26T21:40:19.540Z\",\n" +
            "      \"sessionTemplateId\": \"%s\",\n" +
            "      \"useJitsi\": true\n" +
            "    }\n" +
            "}";

    final static String START_SESSION_VARIABLES = "{\n" +
            "\"id\": \"%s\"\n" +
            "}";

    final static String GENERATE_JITSI_TOKEN_VARIABLES = "{\n" +
            "    \"displayName\": \"%s\",\n" +
            "    \"email\": \"%s\",\n" +
            "    \"roomId\": \"%s\"\n" +
            "}";

    final static String JITSI_LINK_PARAMETERS = "#jitsi_meet_external_api_id=2&interfaceConfig.SHOW_WATERMARK_FOR_GUESTS=false&interfaceConfig.SHOW_BRAND_WATERMARK=false&interfaceConfig.SHOW_POWERED_BY=false&interfaceConfig.DISPLAY_WELCOME_PAGE_CONTENT=false&interfaceConfig.DISPLAY_WELCOME_PAGE_TOOLBAR_ADDITIONAL_CONTENT=false&interfaceConfig.APP_NAME=%22Sozisel%22&interfaceConfig.LANG_DETECTION=true&interfaceConfig.TOOLBAR_BUTTONS=%5B%22microphone%22%2C%22camera%22%2C%22fullscreen%22%2C%22desktop%22%2C%22fodeviceselection%22%2C%22hangup%22%2C%22info%22%2C%22chat%22%2C%22settings%22%2C%22raisehand%22%2C%22videoquality%22%2C%22filmstrip%22%2C%22shortcuts%22%2C%22help%22%2C%22mute-everyone%22%5D&interfaceConfig.VIDEO_LAYOUT_FIT=%22both%22&interfaceConfig.DISABLE_DOMINANT_SPEAKER_INDICATOR=false&interfaceConfig.DISABLE_TRANSCRIPTION_SUBTITLES=true&interfaceConfig.DISABLE_RINGING=true&interfaceConfig.CONNECTION_INDICATOR_AUTO_HIDE_ENABLED=true&interfaceConfig.CONNECTION_INDICATOR_AUTO_HIDE_TIMEOUT=5000&interfaceConfig.DISABLE_PRESENCE_STATUS=false&interfaceConfig.DISABLE_JOIN_LEAVE_NOTIFICATIONS=false&interfaceConfig.HIDE_KICK_BUTTON_FOR_GUESTS=true&appData.localStorageContent=null";
}
