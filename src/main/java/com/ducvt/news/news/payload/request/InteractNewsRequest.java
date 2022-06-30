package com.ducvt.news.news.payload.request;

import com.ducvt.news.news.models.enums.InteractType;
import lombok.Data;

import java.util.Date;

@Data
public class InteractNewsRequest {
    private Long userId;
    private Long newsId;
    private InteractType type;
    private Date interactTime;
}
