package com.ducvt.news.news.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "recommend_news")
public class RecommendNews {
    @Id
    private Long userId;

    private String listNewsId;

    private Date createTime;

    private Date updateTime;
}
