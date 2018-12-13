package com.nobodyhub.transcendence.api.executor.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * A generic data structure to represent topic, like job, company. and etc.
 */
@Data
public class ZhihuApiTopic {
    @JsonProperty("introduction")
    protected String introduction;
    @JsonProperty("avatar_url")
    protected String avatarUrl;
    @JsonProperty("name")
    protected String name;
    @JsonProperty("url")
    protected String url;
    @JsonProperty("type")
    protected String type;
    @JsonProperty("excerpt")
    protected String excerpt;
    @JsonProperty("id")
    protected String id;
}
