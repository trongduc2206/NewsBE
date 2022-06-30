package com.ducvt.news.news.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class InteractNewsId implements Serializable {
    private Long newsId;

    private Long userId;
}
