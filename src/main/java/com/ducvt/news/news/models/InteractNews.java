package com.ducvt.news.news.models;

import com.ducvt.news.news.models.enums.InteractType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "interact_news")
//@IdClass(InteractNewsId.class)
public class InteractNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long newsId;

    private Long userId;

    private Date interactTime;

    private InteractType type;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
