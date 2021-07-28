package vn.acgroup.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

class YandexInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    HttpHeaders headers = request.getHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON.toString());
    headers.setBearerAuth(LightwalletConfig.MAIN_ACC_TOKEN);
    return execution.execute(request, body);
  }
}

@Configuration
public class YandexConfig {
  public static String from = "noreply@daugia.io";
  public static String pass = "abcD123$";

  public static String link = "https://daugia.io/verify/";
  public static String home = "https://daugia.io";
}
