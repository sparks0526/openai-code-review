package com.sparks.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sparks.sdk.domain.service.impl.OpenAiCodeReviewService;
import com.sparks.sdk.infrastructure.git.GitCommand;
import com.sparks.sdk.infrastructure.openai.IOpenAI;
import com.sparks.sdk.infrastructure.openai.impl.ChatGLM;
import com.sparks.sdk.infrastructure.weixin.Weixin;

public class OpenAiCodeReview {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiCodeReview.class);

    // 微信配置
    private String weixin_appid = "wx4359922ace687f92";
    private String weixin_secret = "f59d9cfce7502417ba8519e023c39804";
    private String weixin_touser = "ovGou6uFlamUKpyDlfQVW-QpjmI0";
    private String weixin_template_id = "mFQgwDBTTgRLSWORhXp5qBct05gFtcNI0gamlNDWL5s";

    // ChatGLM 配置
    private String chatglm_apiHost = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private String chatglm_apiKeySecret = "";

    // Github 配置
    private String github_review_log_uri;
    private String github_token;

    // 工程配置 - 自动获取
    private String github_project;
    private String github_branch;
    private String github_author;

    public static void main(String[] args) throws Exception {
        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
        );

        /**
         * 项目：{{repo_name.DATA}} 分支：{{branch_name.DATA}} 作者：{{commit_author.DATA}} 说明：{{commit_message.DATA}}
         */
        Weixin weiXin = new Weixin(
                getEnv("WEIXIN_APPID"),
                getEnv("WEIXIN_SECRET"),
                getEnv("WEIXIN_TOUSER"),
                getEnv("WEIXIN_TEMPLATE_ID")
        );



        IOpenAI openAI = new ChatGLM(getEnv("CHATGLM_APIHOST"), getEnv("CHATGLM_APIKEYSECRET"));

        OpenAiCodeReviewService openAiCodeReviewService = new OpenAiCodeReviewService(gitCommand, openAI, weiXin);
        openAiCodeReviewService.exec();

        logger.info("openai-code-review done!");
    }

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("value is null");
        }
        return value;
    }

}
