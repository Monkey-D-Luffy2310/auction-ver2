package vn.acgroup.config;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity // Enable security config. This annotation denotes config for spring security.
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {

  public static String TOKEN_NAME = "AC-ACCESS-KEY";
  public static String TOKEN_SECRET = "dailamdungcomadoan";
  public static String TOKEN_ISSUER = "AchauAuction";

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.headers().frameOptions().disable();
    String bypass[] = {
      "/auth/**",
      "/",
      "/auction",
      "/dang-nhap",
      "/dau-gia-sap-bat-dau",
      "/dau-gia-da-ket-thuc",
      "/chi-tiet-dau-gia/{id}",
      "/chi-tiet-dau-gia-nguoc",
      "/tat-ca-dau-gia-nguoc",
      "/AC-Auction/chi-tiet-tin-tuc/{id}",
      "/dang-ky",
      "/tim-kiem/**",
      "/gioi-thieu",
      "/danh-muc/{category}/**",
      "/tai-khoan/**",
      "/quen-mat-khau/**",
      "/ho-so-ca-nhan/**",
      "/thay-doi-mat-khau/**",
      "/ho-so-dai-ly/**",
      "/vi-va-so-du/**",
      "/dat-coc-dau-gia/**",
      "/nap-tien/**",
      "/nap-tien-buoc2/**",
      "/rut-tien/**",
      "/rut-tien-buoc2/**",
      "/rut-tien-buoc3/**",
      "/vi-va-so-du",
      "/ho-so-ca-nhan",
      "/danh-muc-dau-gia/**",
      "/dia-chi-van-chuyen",
      "/dau-gia-da-mua",
      "/danh-sach-phien-dau-gia",
      "/dau-gia-da-thang",
      "/dau-gia-dang-tham-du",
      "/dau-gia-yeu-thich",
      "/dau-gia-da-dang-ky",
      "/bang-chinh-dai-ly",
      "/danh-sach-dau-gia",
      "/them-dau-gia/{id}",
      "/danh-sach-tai-san",
      "/them-tai-san",
      "/sua-tai-san/{id}",
      "/sua-mo-ta/{id}",
      "/danh-sach-nguoi-ban",
      "them-nguoi-ban",
      "/user/register",
      "/user/**",
      "/asset",
      "/asset/**",
      "/auction/**",
      "/bid/**",
      "/wallet/**",
      "/category/**",
      "/auction_registration/**",
      "/redisearch/**",
      "/notice_registration/**",
      "/verify/**",
      "/search",
      "/giftcode/**",
      "/AC-Auction/**",
      "/tin-tuc",
      "/AC-Auction/danh-muc-tin-tuc/{id}",
      "/tro-giup",
      "/lien-he",
      "/huong-dan-dang-ky-tai-khoan-san-dau-gia",
      "/dieu-khoan-su-dung",
      "/nhap-giftcode",
      "/huong-dan-tham-gia-dau-gia",
      "/huong-dan-nap-tien-bang-giftcode",
      "/huong-dan-nap-tien-qua-san-tiktak",
      "/huong-dan-cach-rut-tien-tu-san-daugia-io",
      "/google906453a653354218.html",
      "/17917346CC1A57E4257AC907D32DE5D9954B2BB6",
      "/google140cafae841c0a9f.html",
      "/admin/**",
      "/update/information/**",
      "/chuong-trinh-affiliate",
      "/da-dang-ky",
      "/mobile/**",
      "/system-time",
      "/gio-hang"
    };

    String ignore[] = {
      "/v2/api-docs",
      "/csrf",
      "/configuration/ui",
      "/swagger-resources/**",
      "/configuration/security",
      "/swagger-ui.html",
      "/webjars/**",
      "/js/**",
      "/css/**",
      "/images/**",
      "/lib/**",
    };

    http.csrf()
        .disable()
        // make sure we use stateless session; session won't be used to store user's
        // state.
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        // handle an authorized attempts
        .exceptionHandling()
        .authenticationEntryPoint(
            (req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
        .and()
        // Add a filter to validate the tokens with every request
        .addFilterBefore(
            new JwtTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        // authorization requests config
        .authorizeRequests()
        // allow all who are accessing "auth" service
        .antMatchers(null, bypass)
        .permitAll()
        .antMatchers(null, ignore)
        .permitAll()
        .antMatchers(HttpMethod.OPTIONS, "/**")
        .permitAll()
        // Any other request must be authenticated
        .anyRequest()
        .authenticated();
  }
}
