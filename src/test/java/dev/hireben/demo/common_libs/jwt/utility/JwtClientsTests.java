package dev.hireben.demo.common_libs.jwt.utility;

import java.security.KeyPair;

import javax.crypto.SecretKey;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import dev.hireben.demo.common_libs.jwt.JwtIssuer;
import dev.hireben.demo.common_libs.jwt.JwtVerifier;
import io.jsonwebtoken.Jwts;

final class JwtClientsTests {

  private static final String ISSUER_NAME = "hireben.dev";
  private static final SecretKey symmetricKey = Jwts.SIG.HS256.key().build();
  private static final KeyPair keyPair = Jwts.SIG.RS256.keyPair().build();

  // =============================================================================

  @Test
  void testNewVerifier() {
    JwtVerifier expected = new JwtVerifierImpl();
    JwtVerifier actual = JwtClients.newVerifier();

    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  // -----------------------------------------------------------------------------

  @Test
  void testNewVerifierWithSymmetricKey() {
    JwtVerifier expected = new JwtVerifierImpl(symmetricKey);
    JwtVerifier actual = JwtClients.newVerifierWithSymmetricKey(symmetricKey);

    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  // -----------------------------------------------------------------------------

  @Test
  void testNewVerifierWithPublicKey() {
    JwtVerifier expected = new JwtVerifierImpl(keyPair.getPublic());
    JwtVerifier actual = JwtClients.newVerifierWithPublicKey(keyPair.getPublic());

    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  // -----------------------------------------------------------------------------

  @Test
  void testNewIssuer() {
    JwtIssuer expected = new JwtIssuerImpl(ISSUER_NAME);
    JwtIssuer actual = JwtClients.newIssuer(ISSUER_NAME);

    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  // -----------------------------------------------------------------------------

  @Test
  void testNewIssuerWithSymmetricKey() {
    JwtIssuer expected = new JwtIssuerImpl(ISSUER_NAME, symmetricKey);
    JwtIssuer actual = JwtClients.newIssuerWithSymmetricKey(ISSUER_NAME, symmetricKey);

    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  // -----------------------------------------------------------------------------

  @Test
  void testNewIssuerWithPrivateKey() {
    JwtIssuer expected = new JwtIssuerImpl(ISSUER_NAME, keyPair.getPrivate());
    JwtIssuer actual = JwtClients.newIssuerWithPrivateKey(ISSUER_NAME, keyPair.getPrivate());

    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

}
