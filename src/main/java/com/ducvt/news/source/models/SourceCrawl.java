package com.ducvt.news.source.models;

import com.ducvt.news.news.models.InteractNews;
import com.ducvt.news.news.models.Topic;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "source_crawl")
public class SourceCrawl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer sourceId;
    private Integer topicId;
    private String crawlUrl;
    private String crawlTime;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
