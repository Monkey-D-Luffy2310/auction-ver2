package vn.acgroup.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

class VndtInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    HttpHeaders headers = request.getHeaders();
    headers.add("Accept", MediaType.APPLICATION_JSON.toString());
    headers.setBearerAuth(VndtConfig.TOKEN);
    return execution.execute(request, body);
  }
}

@Configuration
public class VndtConfig {
  public static String ACA_ID = "ACA";
  public static String MAIN_ADDRESS = "TMJ65byZaKTSrrPqa7moKEcunBeJvo1c42";
  public static String URL = "LightwalletConfig.java";
  public static String TOKEN = "tokenbimat.naptiendaugia247";
  public static String pincode = "pincodechogiftcodeacwallet12365434abcd";
  public static String acw_tk =
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhY3d0YyIsImp0aSI6IjQ5NDI0NTEifQ.sWjQgYuTS-toDZdSDJ2ibzxcQ36L0kMUEcq_TAeBNa0";
}
