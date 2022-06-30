package com.ducvt.news.news.models.dto;

import lombok.Data;

import java.util.List;

@Data
public class NewsPageDto {
    List<NewsDto> content;
    Long totalElements;
}
