package com.dooars.mountain.service.firebase;

import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.common.BaseException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Prantik Guha on 09-06-2021
 **/
@Service
public class FireBaseService {

    @PostConstruct
    private void init() throws IOException {
        InputStream serviceAccount = this.getClass().getClassLoader()
                .getResourceAsStream("./hotel-dooars-mountain-firebase-adminsdk-ae7i7-1f0e6f57d1.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty())
            FirebaseApp.initializeApp(options);
    }

    public BatchResponse sendPromotion(List<String> tokens, String title, String body) throws BaseException {

        try {
            MulticastMessage multicastMessage = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .addAllTokens(tokens)
                    .build();
            return FirebaseMessaging.getInstance().sendMulticast(multicastMessage);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(e.getMessage(), AllGolbalConstants.SERVICE_LAYER, null);
        }
    }

    public MulticastMessage createMsg(List<String> tokens, String title, String body, Map<String, String> dataMap) {
        return MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .addAllTokens(tokens)
                .putAllData(dataMap)
                .build();
    }

    public BatchResponse sendNotification(MulticastMessage multicastMessage) throws BaseException {

        try {
            return FirebaseMessaging.getInstance().sendMulticast(multicastMessage);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(e.getMessage(), AllGolbalConstants.SERVICE_LAYER, null);
        }
    }
}
