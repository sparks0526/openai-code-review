package com.sparks.sdk.infrastructure.weixin.dto;

import java.util.HashMap;
import java.util.Map;

public class TemplateMessageDTO {

    private String touser = "ovGou6uFlamUKpyDlfQVW-QpjmI0";

    private String template_id = "XJqDQLarurvYHNA_lcMMrf4j0Ify07BgvrAwhZ1xpy8";

    private String url = "https://github.com/sparks0526/openai-code-review-log/blob/main/2025-06-26/Bk7xt6q2cml6.md";

    private Map<String, Map<String, String>> data = new HashMap<>();

    public TemplateMessageDTO(String touser, String template_id) {
        this.touser = touser;
        this.template_id = template_id;
    }
    public void put(String key, String value) {
        data.put(key, new HashMap<String, String>() {
            private static final long serialVersionUID = 7092338402387318563L;

            {
                put("value", value);
            }
        });
    }
    public static void put(Map<String, Map<String, String>> data, TemplateKey key, String value){
        data.put(key.getCode(), new HashMap<String, String>() {
            private static final long serialVersionUID = 7092338402387318563L;

            {
                put("value", value);
            }
        });
    }
    public enum TemplateKey {
        REPO_NAME("repo_name","项目名称"),
        BRANCH_NAME("branch_name","分支名称"),
        COMMIT_AUTHOR("commit_author","提交者"),
        COMMIT_MESSAGE("commit_message","提交信息"),
        ;

        private String code;
        private String desc;

        TemplateKey(String desc, String code) {
            this.desc = desc;
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Map<String, String>> getData() {
        return data;
    }

    public void setData(Map<String, Map<String, String>> data) {
        this.data = data;
    }

}
