package com.ducvt.news.source.payload.request;

import lombok.Data;

@Data
public class CrawlRequest {
    private String url;
    private Integer topicLv1;
    private Integer topicLv2;
    private Integer topicLv3;
}
