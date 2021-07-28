package vn.acgroup.config;

import java.io.IOException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

class LightwalletInterceptor implements ClientHttpRequestInterceptor {

  private String token;

  public LightwalletInterceptor(String token) {
    this.token = token;
  }

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    HttpHeaders headers = request.getHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON.toString());
    headers.setBearerAuth(token);
    return execution.execute(request, body);
  }
}

@Configuration
public class LightwalletConfig {
  public static String URL = "https://lightwallet.appspot.com/accounts";
  public static String MAIN_ACC_TOKEN =
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsaWdodHdhbGxldCIsImp0aSI6ImFkbWluQGRhdWdpYTI0Ny5uZXQifQ.RqwWBwt5YT106Mfs1C9cjE6okNm9fKeg9jNlq54IOGU";

  public static String BONUS_ACC_TOKEN =
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsaWdodHdhbGxldCIsImp0aSI6ImFkbWluQGRhdWdpYS5pbyJ9.BY0FtshyaMaAUfD488uPIOvOx3ZIkDX5yYObTSp6osQ";

  // ví nhận vé
  public static String ticketAddress = "TRugC2hcixbonCNZjbmKjcbtNgK3x7vNUR";

  // ví tiền thu tiền sản phẩm
  public static String productAddress = "TYZ2dAG7obafpCs5ZTseLApiNanGpmCu3t";

  public static long adminId = 10002;
  public static long adminProduct = 10004;

  @Bean
  @Primary
  public RestTemplate mainWalletTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(
        Collections.singletonList(new LightwalletInterceptor(MAIN_ACC_TOKEN)));
    return restTemplate;
  }

  @Bean
  @Qualifier("bonusWalletTemplate")
  public RestTemplate bonusWalletTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(
        Collections.singletonList(new LightwalletInterceptor(BONUS_ACC_TOKEN)));
    return restTemplate;
  }
}
