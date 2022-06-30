package com.ducvt.news.news.payload.request;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long userId;
    private Long newsId;
}
