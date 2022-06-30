package com.ducvt.news.fw.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStatus implements Serializable {

    private static final long serialVersionUID = 1216664062736095390L;

    private String code;

    private String message;
}
