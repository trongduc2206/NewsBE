package com.ducvt.news.source.models;

import lombok.Data;

@Data
public class TaskDefinition {
    private String cronExpression;
    private String url;
    private Integer topicLv1;
    private Integer topicLv2;
    private Integer topicLv3;
}
