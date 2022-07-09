package com.ducvt.news.source.models.dto;

import lombok.Data;

import java.util.List;

@Data
public class SourceCrawlDto {
//    private List<String> topicList;
//    private List<String> crawlUrls;
    private List<TopicCrawl> topicCrawls;
    private String crawlTime;
}
