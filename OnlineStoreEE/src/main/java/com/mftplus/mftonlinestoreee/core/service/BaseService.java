package com.mftplus.mftonlinestoreee.core.service;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;

import java.util.List;
import java.util.Optional;

public interface BaseService<RequestDto, Dto> {

    Dto create(RequestDto request);

    Optional<Dto> findById(Long id);

    List<Dto> findAll();

    PageResult<Dto> findAll(int page, int size);

    Optional<Dto> update(Long id, RequestDto request);

    Optional<Dto> deleteById(Long id);
}
