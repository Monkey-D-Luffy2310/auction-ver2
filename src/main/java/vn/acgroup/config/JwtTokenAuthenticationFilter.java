package vn.acgroup.config;

import static java.util.Collections.emptyList;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
@Order(1)
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {
  Logger log = Logger.getLogger(this.getClass().getName());

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = this.getToken(request);
    if (token == null || token.equals("")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      Algorithm algorithm = Algorithm.HMAC256(SecurityTokenConfig.TOKEN_SECRET);
      JWTVerifier verifier =
          JWT.require(algorithm).withIssuer(SecurityTokenConfig.TOKEN_ISSUER).build();
      DecodedJWT jwt = verifier.verify(token);
      String userId = jwt.getId();
      //		log.info("Logged user id " + userId);
      SecurityContextHolder.getContext()
          .setAuthentication(new UsernamePasswordAuthenticationToken(userId, null, emptyList()));
    } catch (Exception e) {
      log.warning("decode token ex : " + e.getMessage());
    }
    filterChain.doFilter(request, response);
  }

  private String getToken(HttpServletRequest request) {
    if (request.getParameter(SecurityTokenConfig.TOKEN_NAME) != null) {
      return request.getParameter(SecurityTokenConfig.TOKEN_NAME);
    }
    if (request.getHeader(SecurityTokenConfig.TOKEN_NAME) != null) {
      return request.getHeader(SecurityTokenConfig.TOKEN_NAME);
    }
    if (request.getHeader("Authorization") != null) {
      return request.getHeader("Authorization").replaceAll("Bearer", "").trim();
    }
    if (WebUtils.getCookie(request, SecurityTokenConfig.TOKEN_NAME) != null) {
      return WebUtils.getCookie(request, SecurityTokenConfig.TOKEN_NAME).getValue();
    }
    return null;
  }
}
