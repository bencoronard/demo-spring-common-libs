package dev.hireben.demo.common_libs.jwt.api;

import io.jsonwebtoken.Claims;

public interface JwtVerifier {

  Claims verifyToken(String token);

}
