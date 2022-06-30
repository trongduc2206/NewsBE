package com.ducvt.news.fw.exceptions;

public class CustomBusinessLogicException extends ApplicationException{
    private static final long serialVersionUID = 224037125737512097L;

    public CustomBusinessLogicException(String code) {
        super(code);
    }

    public CustomBusinessLogicException(String code, String message) {
        super(code, message);
    }
}
