package com.ducvt.news.news.payload.request;

import lombok.Data;

@Data
public class SaveNewsRequest {
    private Long userId;
    private Long newsId;
}
