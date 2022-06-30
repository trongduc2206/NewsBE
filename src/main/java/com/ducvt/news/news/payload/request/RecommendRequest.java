package com.ducvt.news.news.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class RecommendRequest {
    private List<String> data;
    private Integer recommendNum;
}
