package com.ducvt.news.fw.exceptions;

import com.ducvt.news.fw.constant.MessageEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenExpiredException extends RuntimeException {
    private final String code;

    // The message is used when you have a detail message which is difference code
    // The message is only used to log, is wont show to end user
    public TokenExpiredException(String code, String message) {
        super(message);
        this.code = code;
    }

    public TokenExpiredException(MessageEnum messageEnum) {
        this(messageEnum.getCode(), messageEnum.getMessage());
    }
}
