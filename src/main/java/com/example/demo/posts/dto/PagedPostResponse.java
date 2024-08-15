package com.example.demo.posts.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PagedPostResponse {
    private List<ResponsePostDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PagedPostResponse(Page<ResponsePostDTO> postPage) {
        this.content = postPage.getContent();
        this.pageNo = postPage.getNumber();
        this.pageSize = postPage.getSize();
        this.totalElements = postPage.getTotalElements();
        this.totalPages = postPage.getTotalPages();
        this.last = postPage.isLast();
    }
}