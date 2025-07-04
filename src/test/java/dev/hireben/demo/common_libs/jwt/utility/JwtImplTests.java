package dev.hireben.demo.common_libs.jwt.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import dev.hireben.demo.common_libs.jwt.JwtIssuer;
import dev.hireben.demo.common_libs.jwt.JwtVerifier;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

final class JwtImplTests {

  private static final String ISSUER_NAME = "hireben.dev";
  private static final SecretKey symmetricKey = Jwts.SIG.HS256.key().build();
  private static final KeyPair keyPair = Jwts.SIG.RS256.keyPair().build();

  // =============================================================================

  @Test
  void testJwtVerifierConstructorWithNullSymmetricKey() {
    Exception exception = assertThrows(NullPointerException.class, () -> new JwtVerifierImpl((SecretKey) null));
    assertEquals("Symmetric key must not be null", exception.getMessage());
  }

  // -----------------------------------------------------------------------------

  @Test
  void testJwtVerifierConstructorWithNullPublicKey() {
    Exception exception = assertThrows(NullPointerException.class, () -> new JwtVerifierImpl((PublicKey) null));
    assertEquals("Public key must not be null", exception.getMessage());
  }

  // -----------------------------------------------------------------------------

  @Test
  void testJwtIssuerConstructorWithNullSymmetricKey() {
    Exception exception = assertThrows(NullPointerException.class,
        () -> new JwtIssuerImpl(ISSUER_NAME, (SecretKey) null));
    assertEquals("Symmetric key must not be null", exception.getMessage());
  }

  // -----------------------------------------------------------------------------

  @Test
  void testJwtIssuerConstructorWithNullPrivateKey() {
    Exception exception = assertThrows(NullPointerException.class,
        () -> new JwtIssuerImpl(ISSUER_NAME, (PrivateKey) null));
    assertEquals("Private key must not be null", exception.getMessage());
  }

  // -----------------------------------------------------------------------------

  @Test
  void testIssueAndParseTokenWithoutKey() {
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
  void testIssueAndParseTokenWithSymmetricKey() {
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
  void testIssueAndParseTokenWithAsymmetricKeys() {
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
