package com.ducvt.news.news.models.dto;

import com.ducvt.news.news.models.Topic;
import lombok.Data;

import java.util.List;

@Data
public class TopicDto {
//    private Integer id;
    private String label;
    private String key;
    private List<TopicDto> children;
    private Boolean recommended = false;
}
