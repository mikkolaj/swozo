# Sozisel(jitsi) integration #

This module provides all necesarry files required to build and run a local jitsi meeting.

## Running docker compose ##
To run the meeting locally run the command below while being in the soziel directory. <br>
`docker-compose -f docker-compose.yml --env-file .jitsi_env up`

docker-compose.yml containts 6 docker images, therefore fetching them takes a while. Wait until the soziel container displays a message that the app is up and running at port 4000
![image](https://user-images.githubusercontent.com/72918433/198846569-0bbfa19f-085c-4062-984a-f9290cdaca03.png)

The Dockerfile located in the same directory is used to build a sozisel image locally. However, docker compose uses the image from docker hub, to run everything with only one command.

## Creating and joining a meeting ##
To plan and set up a jitsi metting, there are several steps to follow. The Sozisel directory contains a Sozisel.postman_collection.json with all GraphQL requests needed to be made to the app. While making requests, change only GRAPHQL VARIABLES located on the right site of the Postman app.![image](https://user-images.githubusercontent.com/72918433/198847029-59a64a62-720f-4962-872a-67747b948f44.png)

1. Register
![image](https://user-images.githubusercontent.com/72918433/198847106-31bb5a50-f55d-4645-a177-66c0e7c5747a.png)

2. Login - copy the token from the received response and paste it as a bearer token in the Sozisel Postman directory
![image](https://user-images.githubusercontent.com/72918433/198847159-262ec74c-011d-4df2-8b92-22b89a9a6b5d.png)
![image](https://user-images.githubusercontent.com/72918433/198847263-8edcca1b-c171-4663-ac18-3c47f78327b4.png)

3. Create template - once created template can be used multiple times to create independent sessions.
![image](https://user-images.githubusercontent.com/72918433/198847332-562443b1-618f-477e-b7c3-e3ffab34ff23.png)

4. Plan session - copy the templateId from the response of the previous request and paste it into the `sessionTemplateId` field.
![image](https://user-images.githubusercontent.com/72918433/198847436-a176df80-b2d3-45c3-99f9-4c7127f7f700.png)

5. Start session - replace the id field with a one returned in the previous request.
![image](https://user-images.githubusercontent.com/72918433/198847510-82d93e6a-90cc-425e-8d49-6ed8dafdb425.png)

6. Generate Jitsi Token - paste the session id as the `roomId` field.
![image](https://user-images.githubusercontent.com/72918433/198847575-c578e002-79f7-49de-9ee7-ed013050f64a.png)

7. Build URL - 
To connect to the meeting using your browser, copy the roomId and the Jitsi token from the last request and build a link using the template below:
`https://localhost:8443/` + `$sessionId` + `?jwt=` + `$jitsiToken`  + `#jitsi_meet_external_api_id=2&interfaceConfig.SHOW_WATERMARK_FOR_GUESTS=false&interfaceConfig.SHOW_BRAND_WATERMARK=false&interfaceConfig.SHOW_POWERED_BY=false&interfaceConfig.DISPLAY_WELCOME_PAGE_CONTENT=false&interfaceConfig.DISPLAY_WELCOME_PAGE_TOOLBAR_ADDITIONAL_CONTENT=false&interfaceConfig.APP_NAME=%22Sozisel%22&interfaceConfig.LANG_DETECTION=true&interfaceConfig.TOOLBAR_BUTTONS=%5B%22microphone%22%2C%22camera%22%2C%22fullscreen%22%2C%22desktop%22%2C%22fodeviceselection%22%2C%22hangup%22%2C%22info%22%2C%22chat%22%2C%22settings%22%2C%22raisehand%22%2C%22videoquality%22%2C%22filmstrip%22%2C%22shortcuts%22%2C%22help%22%2C%22mute-everyone%22%5D&interfaceConfig.VIDEO_LAYOUT_FIT=%22both%22&interfaceConfig.DISABLE_DOMINANT_SPEAKER_INDICATOR=false&interfaceConfig.DISABLE_TRANSCRIPTION_SUBTITLES=true&interfaceConfig.DISABLE_RINGING=true&interfaceConfig.CONNECTION_INDICATOR_AUTO_HIDE_ENABLED=true&interfaceConfig.CONNECTION_INDICATOR_AUTO_HIDE_TIMEOUT=5000&interfaceConfig.DISABLE_PRESENCE_STATUS=false&interfaceConfig.DISABLE_JOIN_LEAVE_NOTIFICATIONS=false&interfaceConfig.HIDE_KICK_BUTTON_FOR_GUESTS=true&appData.localStorageContent=null`

Paste the build URL into your browser
![image](https://user-images.githubusercontent.com/72918433/198847776-a64f683a-d69c-47e7-bf58-0716c0346ad4.png)
