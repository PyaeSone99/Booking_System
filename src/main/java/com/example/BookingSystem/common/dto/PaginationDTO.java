package com.example.BookingSystem.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginationDTO<T> (
        List<T> content,
        int pageNumber,
        int pageSize,
        long offset,
        long totalElements,
        int totalPages
){
    public static <T> PaginationDTO<T> from(Page<T> page) {
        return new PaginationDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getPageable().getOffset(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

}