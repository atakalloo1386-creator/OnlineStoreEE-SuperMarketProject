package com.mftplus.mftonlinestoreee.core.dto;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public static <T> PageResult<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageResult<>(content, page, size, totalElements, totalPages);
    }

    public <R> PageResult<R> map(Function<T, R> mapper) {
        return new PageResult<>(content.stream().map(mapper).toList(), page, size, totalElements, totalPages);
    }
}
