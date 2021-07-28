package vn.acgroup.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("vn.acgroup.controllers"))
        .paths(PathSelectors.any())
        .build()
        .directModelSubstitute(ResponseEntity.class, java.lang.Void.class)
        .securitySchemes(Arrays.asList(apiKey()))
        .apiInfo(apiEndPointsInfo());
  }

  private ApiKey apiKey() {
    return new ApiKey("JWT", "Authorization", "header");
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(PathSelectors.regex("/**"))
        .build();
  }

  private List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
  }

  private ApiInfo apiEndPointsInfo() {

    return new ApiInfoBuilder()
        .title("Achau Auction")
        .description("Achau Auction Management REST API")
        .contact(new Contact("Bui Dinh Ngoc", "https://acgroup.vn", "ngocbd@acgroup.vn"))
        .license("Apache 2.0")
        .termsOfServiceUrl("#")
        .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
        .version("0.0.1-SNAPSHOT")
        .build();
  }
}
