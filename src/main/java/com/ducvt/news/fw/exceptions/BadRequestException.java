package com.ducvt.news.fw.exceptions;

import com.ducvt.news.fw.constant.MessageEnum;
import lombok.Getter;

@Getter
public class BadRequestException extends ApplicationException {

    private final MessageEnum messageEnum;
    private final String description;

    public BadRequestException(MessageEnum messageEnum) {
        super(messageEnum.getCode(), messageEnum.getMessage());
        this.messageEnum = messageEnum;
        this.description = null;
    }

    public BadRequestException(MessageEnum messageEnum, String description) {
        super(messageEnum.getCode(), description);
        this.messageEnum = messageEnum;
        this.description = description;
    }

}
