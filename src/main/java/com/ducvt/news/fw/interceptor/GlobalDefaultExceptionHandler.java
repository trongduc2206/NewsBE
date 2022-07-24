package com.ducvt.news.fw.interceptor;

import com.ducvt.news.fw.constant.MessageConstant;
import com.ducvt.news.fw.domain.GeneralResponse;
import com.ducvt.news.fw.domain.ResponseStatus;
import com.ducvt.news.fw.domain.ValidationResponse;
import com.ducvt.news.fw.exceptions.*;
import com.ducvt.news.fw.utils.ResponseFactory;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity defaultExceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, MessageConstant.GENERAL_ERROR);
    }

    @ExceptionHandler(value = TokenExpiredException.class)
    @ResponseBody
    public ResponseEntity jwtExpiredExceptionHandler(TokenExpiredException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.UNAUTHORIZED, MessageConstant.GENERAL_ERROR);
    }

//    @ExceptionHandler(value = FeignException.class)
//    @ResponseBody
//    public ResponseEntity defaultFeignExceptionHandler(FeignException e) {
//        log.error(e.getMessage(), e);
//        return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, MessageConstant.GENERAL_ERROR);
//    }

    @ExceptionHandler(value = ApplicationException.class)
    @ResponseBody
    public ResponseEntity applicationExceptionHandler(ApplicationException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, MessageConstant.GENERAL_ERROR);
    }

    @ExceptionHandler(value = BusinessLogicException.class)
    @ResponseBody
    public ResponseEntity businessLogicExceptionHandler(BusinessLogicException e) {
        return ResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getCode());
    }

    @ExceptionHandler(value = CustomBusinessLogicException.class)
    @ResponseBody
    public ResponseEntity customBusinessLogicExceptionHandler(CustomBusinessLogicException e) {
        GeneralResponse generalResponse = new GeneralResponse();
        generalResponse.setStatus(new ResponseStatus(e.getCode(), e.getMessage()));
        return new ResponseEntity(generalResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity constraintViolationExceptionHandler(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.BAD_REQUEST, MessageConstant.DB_DUPLICATE);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity dataIntegrityViolationExceptionHandler(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.BAD_REQUEST, MessageConstant.DB_DUPLICATE);
    }

    @ExceptionHandler(value = DataException.class)
    @ResponseBody
    public ResponseEntity dataExceptionHandler(DataException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.BAD_REQUEST, MessageConstant.DB_EXCEPTION);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity defaultHttpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.BAD_REQUEST, MessageConstant.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity defaultMethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.debug(e.getMessage());
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        Set<ValidationResponse> responses = new HashSet<>();
        errors.forEach(error -> {
            Object[] arguments = error.getArguments();
            Object[] args = null;
            if (arguments.length > 1) {
                try {
                    args = (Object[]) arguments[1];
                } catch (Exception e2) {
                    args = null;
                }
            }
            String msg = ResponseFactory.getMessFromCache(error.getDefaultMessage());
            if (args != null) {
                msg = String.format(msg, args);
            }

            ValidationResponse response = new ValidationResponse();
            response.setField(error.getField());
            response.setMsg(msg);

            responses.add(response);
        });

        return ResponseFactory.error(HttpStatus.BAD_REQUEST, MessageConstant.BAD_REQUEST, responses);
    }

    @ExceptionHandler(value = ObjectNotFoundException.class)
    @ResponseBody
    public ResponseEntity objectNotFoundExceptionHandler(ObjectNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.OK, e.getCode());
    }

    @ExceptionHandler(value = DocumentException.class)
    @ResponseBody
    public ResponseEntity documentExceptionHandler(DocumentException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.OK, e.getCode());
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity maxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.BAD_REQUEST, MessageConstant.MAX_UPLOAD_SIZE_EXCEED);
    }

    @ExceptionHandler(value = BadRequestException.class)
    @ResponseBody
    public ResponseEntity badRequestException(BadRequestException e) {
        log.error(e.getMessage(), e);
        return ResponseFactory.error(HttpStatus.BAD_REQUEST, e);
    }

//    @ExceptionHandler(value = SHBException.class)
//    @ResponseBody
//    public ResponseEntity shbException(SHBException e) {
//        log.error(e.getMessage(), e);
//        return ResponseFactory.shbError(e);
//    }

}
