package com.mftplus.mftonlinestoreee.products.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class InvalidParentCategoryException extends BusinessException {
    public InvalidParentCategoryException(String parentCategory) {
        super("Invalid ParentCategory"+ parentCategory);
    }
}
