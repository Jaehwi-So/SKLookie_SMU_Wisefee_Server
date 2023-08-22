package com.sklookiesmu.wisefee.service.shared;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.sklookiesmu.wisefee.dto.shared.firebase.FCMNotificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class FCMNotificationService {

    private final FirebaseMessaging firebaseMessaging;

    public String sendNotificationByToken(FCMNotificationRequestDto requestDto) {
        if (requestDto.getTo() != null) {
            Message message = Message.builder()
                    .setToken(requestDto.getTo())
                    .setNotification(Notification.builder()
                            .setTitle(requestDto.getData().get("title"))
                            .setBody(requestDto.getData().get("body"))
                            .build())
                    .putAllData(requestDto.getData())
                    .build();

            try {
                firebaseMessaging.send(message);
                return "알림을 성공적으로 전송했습니다. targetUserToken=" + requestDto.getTo();
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                return "알림 보내기를 실패하였습니다. targetUserToken=" + requestDto.getTo();
            }
        } else {
            return "FCM 토큰 값이 존재하지 않습니다.";
        }
    }
}