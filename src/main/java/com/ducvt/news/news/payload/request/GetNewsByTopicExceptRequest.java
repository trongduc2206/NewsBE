package com.ducvt.news.news.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class GetNewsByTopicExceptRequest {
    private List<Long> newsId;
    private String topicKey;
}
