package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.rmi.ChatRemoteService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.rmi.RemoteException;
import java.util.List;

public final class ChatUtils {

    public static void createAndRunChatTimeline(ChatRemoteService chatRemoteService,
                                                TextArea chatMessagesTextArea)
    {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            try {
                List<String> chatMessages = chatRemoteService.getAllChatMessages();

                chatMessagesTextArea.clear();

                for (String chatMessage : chatMessages) {
                    chatMessagesTextArea.appendText(chatMessage + "\n");
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public static void sendChatMessage(TextField chatMessagesTextField, TextArea chatMessagesTextArea,
                                       ChatRemoteService chatRemoteService) {
        String messageText = chatMessagesTextField.getText();
        try {
            chatRemoteService.sendChatMessage(GreatWesternTrailApplication.playerMode + ": "
                    + messageText);

            List<String> chatMessages = chatRemoteService.getAllChatMessages();
            chatMessagesTextArea.clear();

            for (String chatMessage : chatMessages) {
                chatMessagesTextArea.appendText(chatMessage + "\n");
            }

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}