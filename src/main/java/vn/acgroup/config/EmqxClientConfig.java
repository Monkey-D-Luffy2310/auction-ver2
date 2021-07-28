package vn.acgroup.config;

import java.io.IOException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

class EmqxClientConfigInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    HttpHeaders headers = request.getHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON.toString());
    headers.setBasicAuth(EmqxClientConfig.EMQX_USERNAME, EmqxClientConfig.EMQX_PASSWORD);
    return execution.execute(request, body);
  }
}

@Configuration
public class EmqxClientConfig {
  public static String EMQX_USERNAME = "admin";
  public static String EMQX_PASSWORD = "public";
  public static String EMQX_PUBLISH_URL = "http://34.126.168.214:8081/api/v4/mqtt/publish";

  @Bean
  @Qualifier("emqxClient")
  public RestTemplate emqxClient() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(Collections.singletonList(new EmqxClientConfigInterceptor()));
    return restTemplate;
  }
}
