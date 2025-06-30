package com.sparks.sdk.domain.service;


import com.sparks.sdk.infrastructure.git.GitCommand;
import com.sparks.sdk.infrastructure.openai.IOpenAI;
import com.sparks.sdk.infrastructure.weixin.Weixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public abstract class AbstractIOpenAiCodeReviewService implements IOpenAiCodeReviewService {
    private Logger logger = LoggerFactory.getLogger(AbstractIOpenAiCodeReviewService.class);

    protected final GitCommand gitCommand;
    protected final IOpenAI openAI;
    protected final Weixin weixin;

    public AbstractIOpenAiCodeReviewService(GitCommand gitCommand, IOpenAI openAI, Weixin weixin) {
        this.gitCommand = gitCommand;
        this.openAI = openAI;
        this.weixin = weixin;
    }

    @Override
    public void exec() {
        try {
            // 1. 获取提交代码
            String diffCode = getDiffCode();
            // 2. 开始代码评审
            String recommend = codeReview(diffCode);
            // 3. 记录评审结果，返回日志地址
            String logUrl = recordCodeReview(recommend);
            // 4. 发送模板消息通知
            pushMessage(logUrl);
        } catch (Exception e) {
            logger.error("openAi-code-review-error: ", e);
        }
    }

    protected abstract void pushMessage(String logUrl) throws Exception;

    protected abstract String codeReview(String diffCode) throws Exception;

    protected abstract String recordCodeReview(String recommend) throws Exception;

    protected abstract String getDiffCode() throws IOException, InterruptedException;
}