package com.ducvt.news.source.payload.response;

import lombok.Data;

@Data
public class CrawlResponse {
    private String status;
    private CrawlInformation data;
    private String error;
}
