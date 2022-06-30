package com.ducvt.news.news.models.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private Date createTime;
    private String username;
}
