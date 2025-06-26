package com.sparks.sdk;


import com.alibaba.fastjson2.JSON;
import com.sparks.sdk.domain.model.ChatCompletionRequest;
import com.sparks.sdk.domain.model.ChatCompletionSyncResponse;
import com.sparks.sdk.domain.model.Model;
import com.sparks.sdk.types.utils.BearerTokenUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class OpenAiCodeReview {
    public static void main(String[] args) throws Exception {
        System.out.println("openai代码评审，测试执行");
        String token = System.getenv("GITHUB_TOKEN");
        if(null == token || "".equals(token)){
            throw new Exception("GITHUB_TOKEN<UNK>");
        }

        // 1. 代码检出

        // 创建并设置外部进程构建器
        ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", "HEAD~1", "HEAD");
        processBuilder.directory(new File("."));

        // 启动进程并获取输入流
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // 读取并存储到StringBuilder中
        String line;
        StringBuilder diffCode = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            diffCode.append(line);
        }

        // 等待进程结束并输出退出码
        int exitCode = process.waitFor();
        System.out.println("Exited with code:" + exitCode);

        System.out.println("需要评审的代码：" + diffCode.toString());

        // 2.代码评审
        String log = codeReview(diffCode.toString());
        System.out.println("评审的日志内容为" + log);

        // 3.写入日志文件
        String logUrl = writeLog(token, log);
        System.out.println("write log url:" + logUrl);
    }

    private static String codeReview(String diffCode) throws Exception {
        String apiKeySecret = "9a6c32623975439fa044307cc18c031d.a3mfRxui5Oa6ww5b";
        String token = BearerTokenUtils.getToken(apiKeySecret);

        URL url = new URL("https://open.bigmodel.cn/api/paas/v4/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setDoOutput(true);

        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
        chatCompletionRequest.setModel(Model.GLM_4_FLASH.getCode());
        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequest.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(new ChatCompletionRequest.Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码如下:"));
                add(new ChatCompletionRequest.Prompt("user", diffCode));
            }
        });

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = JSON.toJSONString(chatCompletionRequest).getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }

        int responseCode = connection.getResponseCode();
        System.out.println(responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();

        System.out.println("评审结果：" + content.toString());

        ChatCompletionSyncResponse response = JSON.parseObject(content.toString(), ChatCompletionSyncResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }

    private static String writeLog(String token, String log) throws Exception {
        Git git = Git.cloneRepository()
                .setURI("https://github.com/sparks0526/openai-code-review-log")
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .call();
        // 评审log文件放置在以日期命名的文件夹
        String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File dateFolder = new File("repo/" + dateFolderName);
        if (!dateFolder.exists()) {
            // 如果文件夹不存在，就新建文件夹
            dateFolder.mkdir();
        }

        // 创建log文件的文件名
        String fileName = generateRandomString(12) + ".md";
        File newFile = new File(dateFolder, fileName);
        // 向log文件中写入评审结果
        try (FileWriter writer = new FileWriter(newFile)) {
            writer.write(log);
        }
        git.add().addFilepattern(dateFolderName + "/" + fileName).call();
        git.commit().setMessage("Add new file via GitHub Actions").call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call();

        System.out.println("Changes have been pushed to the repository.");

        return "https://github.com/sparks0526/openai-code-review-log/master/" + dateFolderName + "/" + fileName;


    }

    private static String generateRandomString(int length){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}