package com.ducvt.news.source.models.dto;

import lombok.Data;

import java.util.List;

@Data
public class SourcePageDto {
    List<SourceDto> content;
    Long totalElements;
}
