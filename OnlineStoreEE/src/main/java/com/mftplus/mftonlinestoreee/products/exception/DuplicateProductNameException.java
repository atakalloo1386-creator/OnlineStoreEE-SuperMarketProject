package com.mftplus.mftonlinestoreee.products.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateProductNameException extends BusinessException {
  public DuplicateProductNameException(String product) {
    super("this product : ("+ product + ") already exists");
  }
}
