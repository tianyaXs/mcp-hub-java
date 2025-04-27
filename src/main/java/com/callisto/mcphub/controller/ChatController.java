package com.callisto.mcphub.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder, ToolCallbackProvider tools) {
        this.chatClient = builder.defaultTools(tools).build();
    }

    @GetMapping("/chat")
    public String chat(String input) {
        return this.chatClient.prompt()
                .user(input)
                .call()
                .content();
    }
}
