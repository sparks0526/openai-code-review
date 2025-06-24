package com.sparks.sdk;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class OpenAiCodeReview {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("测试执行");

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

        System.out.println("评审代码：" + diffCode.toString());
    }
}