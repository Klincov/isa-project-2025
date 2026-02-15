package com.example.demo.controller;

import com.example.demo.dto.ChatMessage;
import com.example.demo.service.UserService;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final UserService userService;

    @MessageMapping("/chat/{postId}")
    @SendTo("/topic/chat/{postId}")
    public ChatMessage send(
            @DestinationVariable Long postId,
            ChatMessage message,
            Authentication auth
    ) {
        if(auth == null) message.setUsername("guest");
        else message.setUsername(userService.getUsernameFromEmail(auth.getName()));
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
}

