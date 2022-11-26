package com.swozo.orchestrator.cloud.software.sozisel;

public interface SoziselRequestTemplate {
    interface QUERY {
        String REGISTER = "mutation Register($input: RegisterInput!) {\n" +
                "    register(input: $input) {\n" +
                "        id\n" +
                "        __typename\n" +
                "        }\n" +
                "}";

        String LOGIN = "mutation Login($input: LoginInput!) {\n" +
                "    login(input: $input) {\n" +
                "        token\n" +
                "        __typename\n" +
                "    }\n" +
                "}";

        String CREATE_SESSION_TEMPLATE = "mutation CreateSessionTemplate($input: CreateSessionTemplateInput!) {\n" +
                "    createSessionTemplate(input: $input) {\n" +
                "        id\n" +
                "        __typename\n" +
                "    }\n" +
                "}";

        String PLAN_SESSION = "mutation CreateSession($input: CreateSessionInput!) {\n" +
                "    createSession(input: $input) {\n" +
                "        id\n" +
                "        __typename\n" +
                "    }\n" +
                "}";

        String START_SESSION = "mutation StartSession($id: ID!) {\n" +
                "    startSession(id: $id) {\n" +
                "        id\n" +
                "        __typename\n" +
                "    }\n" +
                "}";

        String GENERATE_JITSI_TOKEN = "query GenerateJitsiToken($displayName: String!, $email: String!, $roomId: ID!) {\n" +
                "    generateJitsiToken(displayName: $displayName, email: $email, roomId: $roomId) {\n" +
                "        token\n" +
                "        email\n" +
                "        displayName\n" +
                "        __typename\n" +
                "    }\n" +
                "}";
    }

    interface VARIABLES {
        String REGISTER = "{\n" +
                "    \"input\": {\n" +
                "      \"email\": \"%s\",\n" +
                "      \"firstName\": \"%s\",\n" +
                "      \"lastName\": \"%s\",\n" +
                "      \"password\": \"SuperSecretPassword\"\n" +
                "    }\n" +
                "}";

        String LOGIN = "{\n" +
                "    \"input\": {\n" +
                "      \"email\": \"%s\",\n" +
                "      \"password\": \"SuperSecretPassword\"\n" +
                "    }\n" +
                "}";

        String CREATE_SESSION_TEMPLATE = "{\n" +
                "    \"input\": {\n" +
                "      \"isPublic\": false,\n" +
                "      \"estimatedTime\": %d,\n" +
                "      \"agendaEntries\": [],\n" +
                "      \"name\": \"Jitsi meeting\"\n" +
                "    }\n" +
                "}";

        String PLAN_SESSION = "{\n" +
                "    \"input\": {\n" +
                "      \"entryPassword\": \"hello_im_a_password\",\n" +
                "      \"name\": \"Session name\",\n" +
                "      \"scheduledStartTime\": \"2022-10-26T21:40:19.540Z\",\n" +
                "      \"sessionTemplateId\": \"%s\",\n" +
                "      \"useJitsi\": true\n" +
                "    }\n" +
                "}";

        String START_SESSION = "{\n" +
                "\"id\": \"%s\"\n" +
                "}";

        String GENERATE_JITSI_TOKEN = "{\n" +
                "    \"displayName\": \"%s\",\n" +
                "    \"email\": \"%s\",\n" +
                "    \"roomId\": \"%s\"\n" +
                "}";
    }

    interface PARAMETERS {
        String TEACHER_LINK = "#jitsi_meet_external_api_id=2&interfaceConfig.SHOW_WATERMARK_FOR_GUESTS=false&interfaceConfig.SHOW_BRAND_WATERMARK=false&interfaceConfig.SHOW_POWERED_BY=false&interfaceConfig.DISPLAY_WELCOME_PAGE_CONTENT=false&interfaceConfig.DISPLAY_WELCOME_PAGE_TOOLBAR_ADDITIONAL_CONTENT=false&interfaceConfig.APP_NAME=%22Sozisel%22&interfaceConfig.LANG_DETECTION=true&interfaceConfig.TOOLBAR_BUTTONS=%5B%22microphone%22%2C%22camera%22%2C%22fullscreen%22%2C%22desktop%22%2C%22fodeviceselection%22%2C%22hangup%22%2C%22info%22%2C%22chat%22%2C%22settings%22%2C%22raisehand%22%2C%22videoquality%22%2C%22filmstrip%22%2C%22shortcuts%22%2C%22help%22%2C%22mute-everyone%22%5D&interfaceConfig.VIDEO_LAYOUT_FIT=%22both%22&interfaceConfig.DISABLE_DOMINANT_SPEAKER_INDICATOR=false&interfaceConfig.DISABLE_TRANSCRIPTION_SUBTITLES=true&interfaceConfig.DISABLE_RINGING=true&interfaceConfig.CONNECTION_INDICATOR_AUTO_HIDE_ENABLED=true&interfaceConfig.CONNECTION_INDICATOR_AUTO_HIDE_TIMEOUT=5000&interfaceConfig.DISABLE_PRESENCE_STATUS=false&interfaceConfig.DISABLE_JOIN_LEAVE_NOTIFICATIONS=false&interfaceConfig.HIDE_KICK_BUTTON_FOR_GUESTS=true&appData.localStorageContent=null";

        String STUDENT_LINK = "#jitsi_meet_external_api_id=0&amp;interfaceConfig.SHOW_WATERMARK_FOR_GUESTS=false&amp;interfaceConfig.SHOW_BRAND_WATERMARK=false&amp;interfaceConfig.SHOW_POWERED_BY=false&amp;interfaceConfig.DISPLAY_WELCOME_PAGE_CONTENT=false&amp;interfaceConfig.DISPLAY_WELCOME_PAGE_TOOLBAR_ADDITIONAL_CONTENT=false&amp;interfaceConfig.APP_NAME=%22Sozisel%22&amp;interfaceConfig.LANG_DETECTION=true&amp;interfaceConfig.TOOLBAR_BUTTONS=%5B%22microphone%22%2C%22camera%22%2C%22fullscreen%22%2C%22desktop%22%2C%22fodeviceselection%22%2C%22hangup%22%2C%22info%22%2C%22chat%22%2C%22settings%22%2C%22raisehand%22%2C%22videoquality%22%2C%22filmstrip%22%2C%22shortcuts%22%2C%22help%22%2C%22mute-everyone%22%5D&amp;interfaceConfig.VIDEO_LAYOUT_FIT=%22both%22&amp;interfaceConfig.DISABLE_DOMINANT_SPEAKER_INDICATOR=false&amp;interfaceConfig.DISABLE_TRANSCRIPTION_SUBTITLES=true&amp;interfaceConfig.DISABLE_RINGING=true&amp;interfaceConfig.CONNECTION_INDICATOR_AUTO_HIDE_ENABLED=true&amp;interfaceConfig.CONNECTION_INDICATOR_AUTO_HIDE_TIMEOUT=5000&amp;interfaceConfig.DISABLE_PRESENCE_STATUS=false&amp;interfaceConfig.DISABLE_JOIN_LEAVE_NOTIFICATIONS=false&amp;interfaceConfig.HIDE_KICK_BUTTON_FOR_GUESTS=true&amp;appData.localStorageContent=%22%7B%5C%22features%2Fprejoin%5C%22%3A%5C%22%7B%5C%5C%5C%22skipPrejoinOnReload%5C%5C%5C%22%3Afalse%7D%5C%22%2C%5C%22features%2Fbase%2Fknown-domains%5C%22%3A%5C%22%5B%5C%5C%5C%22alpha.jitsi.net%5C%5C%5C%22%2C%5C%5C%5C%22beta.meet.jit.si%5C%5C%5C%22%2C%5C%5C%5C%22meet.jit.si%5C%5C%5C%22%2C%5C%5C%5C%228x8.vc%5C%5C%5C%22%2C%5C%5C%5C%22localhost%3A8443%5C%5C%5C%22%5D%5C%22%2C%5C%22features%2Fbase%2Fsettings%5C%22%3A%5C%22%7B%5C%5C%5C%22displayName%5C%5C%5C%22%3A%5C%5C%5C%22%5C%5C%5C%22%2C%5C%5C%5C%22email%5C%5C%5C%22%3A%5C%5C%5C%22%5C%5C%5C%22%2C%5C%5C%5C%22disableSelfView%5C%5C%5C%22%3Afalse%2C%5C%5C%5C%22localFlipX%5C%5C%5C%22%3Atrue%2C%5C%5C%5C%22hideShareAudioHelper%5C%5C%5C%22%3Afalse%2C%5C%5C%5C%22soundsIncomingMessage%5C%5C%5C%22%3Atrue%2C%5C%5C%5C%22soundsParticipantJoined%5C%5C%5C%22%3Atrue%2C%5C%5C%5C%22soundsParticipantLeft%5C%5C%5C%22%3Atrue%2C%5C%5C%5C%22soundsTalkWhileMuted%5C%5C%5C%22%3Atrue%2C%5C%5C%5C%22soundsReactions%5C%5C%5C%22%3Atrue%2C%5C%5C%5C%22startAudioOnly%5C%5C%5C%22%3Afalse%2C%5C%5C%5C%22startWithAudioMuted%5C%5C%5C%22%3Afalse%2C%5C%5C%5C%22startWithVideoMuted%5C%5C%5C%22%3Afalse%2C%5C%5C%5C%22userSelectedNotifications%5C%5C%5C%22%3A%7B%5C%5C%5C%22notify.chatMessages%5C%5C%5C%22%3Atrue%7D%7D%5C%22%2C%5C%22features%2Fcalendar-sync%5C%22%3A%5C%22%7B%7D%5C%22%2C%5C%22features%2Fdropbox%5C%22%3A%5C%22%7B%7D%5C%22%2C%5C%22features%2Frecent-list%5C%22%3A%5C%22%5B%7B%5C%5C%5C%22conference%5C%5C%5C%22%3A%5C%5C%5C%22https%3A%2F%2Flocalhost%3A8443%2F8f91ac25-1f2f-4850-a84c-554607fcb2ab%3Fjwt%3DeyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqaXRzaSIsImNvbnRleHQiOnsidXNlciI6eyJlbWFpbCI6ImR1cGFAZHVwYS5wbCIsImlkIjoiMDc4YzdmODYtNDMwYS00MWYwLWI0MGMtMDNhYjRlMGI2NjRlIiwibmFtZSI6InF3ciAifX0sImV4cCI6MTY2MjI1NzEwNSwiaXNzIjoic296aXNlbF9hcHAiLCJyb29tIjoiOGY5MWFjMjUtMWYyZi00ODUwLWE4NGMtNTU0NjA3ZmNiMmFiIiwic3ViIjoiKiJ9.lYpRVKkQCV1mvXlBgyAp-1Ei09qx-FhBw84AeG0t21o%23jitsi_meet_external_api_id%3D0%26interfaceConfig.SHOW_WATERMARK_FOR_GUESTS%3Dfalse%26interfaceConfig.SHOW_BRAND_WATERMARK%3Dfalse%26interfaceConfig.SHOW_POWERED_BY%3Dfalse%26interfaceConfig.DISPLAY_WELCOME_PAGE_CONTENT%3Dfalse%26interfaceConfig.DISPLAY_WELCOME_PAGE_TOOLBAR_ADDITIONAL_CONTENT%3Dfalse%26interfaceConfig.APP_NAME%3D%2522Sozisel%2522%26interfaceConfig.LANG_DETECTION%3Dtrue%26interfaceConfig.TOOLBAR_BUTTONS%3D%255B%2522microphone%2522%252C%2522camera%2522%252C%2522fullscreen%2522%252C%2522desktop%2522%252C%2522fodeviceselection%2522%252C%2522hangup%2522%252C%2522info%2522%252C%2522chat%2522%252C%2522settings%2522%252C%2522raisehand%2522%252C%2522videoquality%2522%252C%2522filmstrip%2522%252C%2522shortcuts%2522%252C%2522help%2522%252C%2522mute-everyone%2522%255D%26interfaceConfig.VIDEO_LAYOUT_FIT%3D%2522both%2522%26interfaceConfig.DISABLE_DOMINANT_SPEAKER_INDICATOR%3Dfalse%26interfaceConfig.DISABLE_TRANSCRIPTION_SUBTITLES%3Dtrue%26interfaceConfig.DISABLE_RINGING%3Dtrue%26interfaceConfig.CONNECTION_INDICATOR_AUTO_HIDE_ENABLED%3Dtrue%26interfaceConfig.CONNECTION_INDICATOR_AUTO_HIDE_TIMEOUT%3D5000%26interfaceConfig.DISABLE_PRESENCE_STATUS%3Dfalse%26interfaceConfig.DISABLE_JOIN_LEAVE_NOTIFICATIONS%3Dfalse%26interfaceConfig.HIDE_KICK_BUTTON_FOR_GUESTS%3Dtrue%26appData.localStorageContent%3Dnull%5C%5C%5C%22%2C%5C%5C%5C%22date%5C%5C%5C%22%3A1662221106800%2C%5C%5C%5C%22duration%5C%5C%5C%22%3A0%7D%5D%5C%22%2C%5C%22features%2Fvideo-quality-persistent-storage%5C%22%3A%5C%22%7B%7D%5C%22%2C%5C%22features%2Fvirtual-background%5C%22%3A%5C%22%7B%7D%5C%22%2C%5C%22callStatsUserName%5C%22%3A%5C%22Beth-XUy%5C%22%2C%5C%22jitsiMeetId%5C%22%3A%5C%2276b46c25f5b8bc1b3785ed69f1d67067%5C%22%7D%22";
    }
}
