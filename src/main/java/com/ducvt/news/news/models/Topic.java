package com.ducvt.news.news.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String topicKey;

    private String parentKey;

    private Integer level;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
