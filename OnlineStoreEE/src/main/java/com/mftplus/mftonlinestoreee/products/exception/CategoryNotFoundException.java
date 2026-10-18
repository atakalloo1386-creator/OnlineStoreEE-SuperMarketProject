package com.mftplus.mftonlinestoreee.products.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class CategoryNotFoundException extends BusinessException {
  public CategoryNotFoundException(String category) {
    super(category + "Not Found" );
  }
}
