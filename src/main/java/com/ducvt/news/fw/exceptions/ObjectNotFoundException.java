package com.ducvt.news.fw.exceptions;

import lombok.Getter;

@Getter
public class ObjectNotFoundException extends ApplicationException {
    public ObjectNotFoundException(String code) {
        super(code);
    }

    public ObjectNotFoundException(String code, String message) {
        super(code, message);
    }
}
