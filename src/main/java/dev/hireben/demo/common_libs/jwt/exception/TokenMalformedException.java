package dev.hireben.demo.common_libs.jwt.exception;

import dev.hireben.demo.common_libs.exception.ApplicationException;

public final class TokenMalformedException extends ApplicationException {

  public TokenMalformedException(String message) {
    super(message);
  }

}
