package com.ducvt.news.fw.exceptions;

/**
 * @author brian
 * @version 1.0
 * @since 05/07/2019
 */

public class BusinessLogicException extends ApplicationException {
    public BusinessLogicException(String code) {
        super(code);
    }

    public BusinessLogicException(String code, String message) {
        super(code, message);
    }
}
