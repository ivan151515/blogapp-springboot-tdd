package com.blogapp.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum Error {

    USER_NOT_FOUND("user not found", HttpStatus.NOT_FOUND),
    BLOG_NOT_FOUND("such blog not found", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND("comment not found", HttpStatus.NOT_FOUND),
    ACTION_NOT_ALLOWED("action not allowed", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus status;

    Error(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
