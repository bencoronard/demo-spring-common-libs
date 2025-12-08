package dev.hireben.demo.common_libs.http.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import dev.hireben.demo.common_libs.constant.MessageHeader;
import dev.hireben.demo.common_libs.http.annotation.HttpAuthorizationHeader;
import dev.hireben.demo.common_libs.jwt.JwtVerifier;
import dev.hireben.demo.common_libs.jwt.exception.TokenMalformedException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class HttpAuthorizationHeaderResolver implements HandlerMethodArgumentResolver {

  private final JwtVerifier verifier;

  // =============================================================================

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(Claims.class)
        && parameter.hasParameterAnnotation(HttpAuthorizationHeader.class);
  }

  // -----------------------------------------------------------------------------

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) throws Exception {

    String header = webRequest.getHeader(MessageHeader.AUTHORIZATION);

    if (header == null || header.isBlank()) {
      throw new MissingRequestHeaderException(MessageHeader.AUTHORIZATION, parameter);
    }

    Claims claims = verifier.verifyToken(header.substring("Bearer ".length()));
    String principalId = claims.getSubject();

    if (principalId == null || principalId.isBlank() || !principalId.matches("^\\d+$")) {
      throw new TokenMalformedException("Token is malformed");
    }

    return claims;
  }

}
