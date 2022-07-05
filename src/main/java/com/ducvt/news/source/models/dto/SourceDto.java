package com.ducvt.news.source.models.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SourceDto {
    private Integer id;
    private String name;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer mode;
    private Integer frequency;
    private List<SourceCrawlDto> sourceCrawls;

}
