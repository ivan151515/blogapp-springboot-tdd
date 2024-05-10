package com.blogapp.blog.dto;

import java.util.List;

import lombok.Value;

@Value
public class MultipleBlogsRestModel {
    List<BlogsInfoDTO> blogs;
}
