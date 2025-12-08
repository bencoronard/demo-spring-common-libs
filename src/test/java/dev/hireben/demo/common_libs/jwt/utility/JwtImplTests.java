package dev.hireben.demo.common_libs.jwt.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import javax.crypto.SecretKey;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import dev.hireben.demo.common_libs.jwt.JwtIssuer;
import dev.hireben.demo.common_libs.jwt.JwtVerifier;
import dev.hireben.demo.common_libs.jwt.exception.TokenIssuanceFailException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

final class JwtImplTests {

  private static final String ISSUER_NAME = "hireben.dev";
  private static final SecretKey symmetricKey = Jwts.SIG.HS256.key().build();
  private static final KeyPair keyPair = Jwts.SIG.RS256.keyPair().build();

  // =============================================================================

  @Test
  void constructJwtVerifierImpl_withNullSymmetricKey_shouldThrowException() {
    Exception exception = assertThrows(NullPointerException.class, () -> new JwtVerifierImpl((SecretKey) null));
    assertEquals("Symmetric key must not be null", exception.getMessage());
  }

  // -----------------------------------------------------------------------------

  @Test
  void constructJwtVerifierImpl_withNullPublicKey_shouldThrowException() {
    Exception exception = assertThrows(NullPointerException.class, () -> new JwtVerifierImpl((PublicKey) null));
    assertEquals("Public key must not be null", exception.getMessage());
  }

  // -----------------------------------------------------------------------------

  @Test
  void constructJwtIssuerImpl_withNullSymmetricKey_shouldThrowException() {
    Exception exception = assertThrows(NullPointerException.class,
        () -> new JwtIssuerImpl(ISSUER_NAME, (SecretKey) null));
    assertEquals("Symmetric key must not be null", exception.getMessage());
  }

  // -----------------------------------------------------------------------------

  @Test
  void constructJwtIssuerImpl_withNullPrivateKey_shouldThrowException() {
    Exception exception = assertThrows(NullPointerException.class,
        () -> new JwtIssuerImpl(ISSUER_NAME, (PrivateKey) null));
    assertEquals("Private key must not be null", exception.getMessage());
  }

  // -----------------------------------------------------------------------------

  @Test
  void issueToken_withoutKey_withInvalidTtl_shouldThrowException() {
    JwtIssuer issuer = new JwtIssuerImpl(ISSUER_NAME);
    assertThrows(TokenIssuanceFailException.class,
        () -> issuer.issueToken(null, null, null, Duration.ofSeconds(-1), null));
  }

  // -----------------------------------------------------------------------------

  @Test
  void issueToken_withSymmKey_withInvalidTtl_shouldThrowException() {
    JwtIssuer issuer = new JwtIssuerImpl(ISSUER_NAME, symmetricKey);
    assertThrows(TokenIssuanceFailException.class,
        () -> issuer.issueToken(null, null, null, Duration.ofSeconds(-1), null));
  }

  // -----------------------------------------------------------------------------

  @Test
  void issueToken_withAsymmKey_withInvalidTtl_shouldThrowException() {
    JwtIssuer issuer = new JwtIssuerImpl(ISSUER_NAME, keyPair.getPrivate());
    assertThrows(TokenIssuanceFailException.class,
        () -> issuer.issueToken(null, null, null, Duration.ofSeconds(-1), null));
  }

  // -----------------------------------------------------------------------------

  @Test
  void issueToken_withoutKey_shouldBeParsableWithUnsecuredVerifier() {
    JwtIssuer issuer = new JwtIssuerImpl(ISSUER_NAME);
    JwtVerifier verifier = new JwtVerifierImpl();

    String token = issuer.issueToken(null, null, null, null, null);
    Assertions.assertThat(token).isNotBlank();

    Claims claims = verifier.verifyToken(token);
    assertNotNull(claims);
    assertNotNull(claims.getId());
    assertNotNull(claims.getIssuedAt());
  }

  // -----------------------------------------------------------------------------

  @Test
  void issueToken_withSymmKey_shouldBeParsableWithSymmVerifier() {
    JwtIssuer issuer = new JwtIssuerImpl(ISSUER_NAME, symmetricKey);
    JwtVerifier verifier = new JwtVerifierImpl(symmetricKey);

    String token = issuer.issueToken(null, null, null, null, null);
    Assertions.assertThat(token).isNotBlank();

    Claims claims = verifier.verifyToken(token);
    assertNotNull(claims);
    assertNotNull(claims.getId());
    assertNotNull(claims.getIssuedAt());
  }

  // -----------------------------------------------------------------------------

  @Test
  void issueToken_withAsymmKey_shouldBeParsableWithAsymmVerifier() {
    JwtIssuer issuer = new JwtIssuerImpl(ISSUER_NAME, keyPair.getPrivate());
    JwtVerifier verifier = new JwtVerifierImpl(keyPair.getPublic());

    String token = issuer.issueToken(null, null, null, null, null);
    Assertions.assertThat(token).isNotBlank();

    Claims claims = verifier.verifyToken(token);
    assertNotNull(claims);
    assertNotNull(claims.getId());
    assertNotNull(claims.getIssuedAt());
  }

}
