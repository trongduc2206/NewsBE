package com.ducvt.news.news.models;

import com.ducvt.news.account.models.User;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "news_id", referencedColumnName = "id")
    private News news;
    private String content;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
