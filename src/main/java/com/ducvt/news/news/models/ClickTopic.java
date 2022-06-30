package com.ducvt.news.news.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "click_topic")
public class ClickTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Integer topicId;

    private Date clickTime;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
