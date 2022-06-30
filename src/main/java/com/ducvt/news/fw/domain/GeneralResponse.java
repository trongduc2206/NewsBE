package com.ducvt.news.fw.domain;

import lombok.Data;

@Data
public class GeneralResponse<T> {
    private static final long serialVersionUID = 1L;
    private ResponseStatus status;
    private T data;
}
