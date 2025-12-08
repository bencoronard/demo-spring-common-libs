package dev.hireben.demo.common_libs.jwt.exception;

import dev.hireben.demo.common_libs.exception.ApplicationException;

public final class TokenIssuanceFailException extends ApplicationException {

  public TokenIssuanceFailException(String message) {
    super(message);
  }

}
