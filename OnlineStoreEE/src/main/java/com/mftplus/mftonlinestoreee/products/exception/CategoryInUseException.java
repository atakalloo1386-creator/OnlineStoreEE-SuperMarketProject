package com.mftplus.mftonlinestoreee.products.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class CategoryInUseException extends BusinessException {
  public CategoryInUseException(String message) {
    super(message);
  }
}
