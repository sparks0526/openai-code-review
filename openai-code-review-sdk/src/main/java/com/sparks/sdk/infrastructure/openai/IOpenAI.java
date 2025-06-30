package com.sparks.sdk.infrastructure.openai;


import com.sparks.sdk.infrastructure.openai.dto.ChatCompletionRequestDTO;
import com.sparks.sdk.infrastructure.openai.dto.ChatCompletionSyncResponseDTO;

public interface IOpenAI {

    ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO chatRequestDTO) throws Exception;
}