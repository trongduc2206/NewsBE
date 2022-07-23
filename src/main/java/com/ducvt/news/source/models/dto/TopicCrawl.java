package com.ducvt.news.source.models.dto;

import lombok.Data;

@Data
public class TopicCrawl {
    private String topicName;
    private String topicKey;
    private String crawlUrl;
}
