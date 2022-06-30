package com.ducvt.news.news.payload.request;

import lombok.Data;

import java.util.Date;

@Data
public class ClickTopicRequest {
    private Long userId;
//    private Integer topicId;
    private String topicKey;
}
