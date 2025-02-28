package com.example.spring_boot_react_demo.config;

//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.services.youtube.YouTube;
//import com.google.api.services.youtube.YouTubeScopes;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.Arrays;
//import java.util.List;
public class GoogleOAuth2Config {
//    private static final String CLIENT_SECRETS = "credentials.json";  // Đường dẫn đến credentials.json
//    private static final String APPLICATION_NAME = "YouTubeUploader";
//    private static final JsonFactory JSON_FACTORY = com.google.api.client.json.JsonFactory.getDefaultInstance();
//    private static final List<String> SCOPES = Arrays.asList(com.google.api.services.youtube.YouTubeScopes.YOUTUBE_UPLOAD);
//
//    @Bean
//    public YouTube youtubeService() throws Exception {
//        Credential credential = authorize();
//        return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }
//
//    // Hàm xác thực OAuth 2.0
//    private Credential authorize() throws Exception {
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(CLIENT_SECRETS));
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
//                .setAccessType("offline")
//                .build();
//
//        return new AuthorizationCodeInstalledApp(flow, new GooglePromptReceiver()).authorize("user");
//    }
}
