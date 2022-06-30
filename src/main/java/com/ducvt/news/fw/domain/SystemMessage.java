package com.ducvt.news.fw.domain;

import lombok.Data;

import java.util.Date;

@Data
public class SystemMessage {
    private Long id;
    private Date createdTime;
    private Long creatorId;
    private Date lastUpdatedTime;
    private Long lastUpdatedId;
    private Boolean isActivated;
    private Boolean isDeleted;
    private String srtCode;
    private String strMessageVi;
    private String strMessageEn;
    private String strReferenceCode;

}
