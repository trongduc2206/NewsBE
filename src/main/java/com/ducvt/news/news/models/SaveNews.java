package com.ducvt.news.news.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "save_news")
@IdClass(SaveNewsId.class)
public class SaveNews {
    @Id
    private Long newsId;
    @Id
    private Long userId;

    private Date saveTime;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
