package com.mftplus.mftonlinestoreee.products.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateCategoryTitleException extends BusinessException {
    public DuplicateCategoryTitleException(String title) {
        super("this category : ("+ title + ") already exists");
    }
}
