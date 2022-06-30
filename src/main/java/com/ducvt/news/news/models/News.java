package com.ducvt.news.news.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @ManyToOne
    @JoinColumn(name = "topic_id_lv1", referencedColumnName = "id")
    private Topic topicLv1;
    @ManyToOne
    @JoinColumn(name = "topic_id_lv2", referencedColumnName = "id")
    private Topic topicLv2;
    @ManyToOne
    @JoinColumn(name = "topic_id_lv3", referencedColumnName = "id")
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
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "news")
    private List<Comment> comments;
}
