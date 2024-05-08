package com.blogapp.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum Error {

    USER_NOT_FOUND("user not found", HttpStatus.NOT_FOUND),
    BLOG_NOT_FOUND("such follow not found", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND("article not found", HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatus status;

    Error(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
