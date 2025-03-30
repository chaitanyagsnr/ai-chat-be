package com.example.llamachat.controller;

import com.example.llamachat.dto.ChatRequestDto;
import com.example.llamachat.dto.ChatResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.util.concurrent.ExecutionException;

@RestController
public class LlamaChatController {

    @Autowired
    private BedrockRuntimeClient client;

    @Autowired
    private BedrockRuntimeAsyncClient asyncClient;

    private final String modelId = "meta.llama3-8b-instruct-v1:0";

    @PostMapping(path = "/chat")
    @CrossOrigin(origins = "http://localhost:4200")
    public ChatResponseDto chat(@RequestBody ChatRequestDto chatRequest) throws ExecutionException, InterruptedException {
        Message message = Message.builder()
                .content(ContentBlock.fromText(chatRequest.prompt()))
                .role(ConversationRole.USER)
                .build();

        ConverseResponse response = client.converse(request -> request
                .modelId(modelId)
                .messages(message)
                .inferenceConfig(config -> config
                        .maxTokens(1000)
                        .temperature(0.8F)
                        .topP(0.9F)));


        // Retrieve the generated text from Bedrock's response object.
        return new ChatResponseDto(response.output().message().content().get(0).text());
    }
}
