package com.ducvt.news.source.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "source")
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer frequency;
    private Integer mode;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
