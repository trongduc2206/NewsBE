package com.ducvt.news.news.models.dto;

import com.ducvt.news.news.models.Topic;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class NewsDto {
    private Long id;
    private String title;
    private Topic topicLv1;
    private Topic topicLv2;
    private Topic topicLv3;
    private String content;
    private String imageUrl;
    private String summary;
    private Date pubDate;
    private Integer sourceId;
    private String keyword;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private List<CommentDto> comments;
    private Date savedTime = null;
}
