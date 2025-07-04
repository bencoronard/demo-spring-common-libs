package dev.hireben.demo.common_libs.http.utility;

import java.time.Duration;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RestClients {

  public RestClient newClient(
      Integer connTimeout,
      Integer readTimeout,
      Integer connReqTimeout) {

    PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
    manager.setMaxTotal(10000);
    manager.setDefaultMaxPerRoute(10000);

    CloseableHttpClient client = HttpClients.custom()
        .setConnectionManager(manager)
        .build();

    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);
    factory.setConnectTimeout(Duration.ofSeconds(connTimeout));
    factory.setReadTimeout(Duration.ofSeconds(readTimeout));
    factory.setConnectionRequestTimeout(Duration.ofSeconds(connReqTimeout));

    return RestClient.builder()
        .requestFactory(factory)
        .build();
  }

}
