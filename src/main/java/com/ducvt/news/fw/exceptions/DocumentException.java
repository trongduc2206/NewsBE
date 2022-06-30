package com.ducvt.news.fw.exceptions;

import lombok.Getter;

@Getter
public class DocumentException extends ApplicationException {
    public DocumentException(String code) {
        super(code);
    }

    public DocumentException(String code, String message) {
        super(code, message);
    }
}
