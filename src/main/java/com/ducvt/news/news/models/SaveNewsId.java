package com.ducvt.news.news.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class SaveNewsId implements Serializable {
    private Long newsId;

    private Long userId;
}
