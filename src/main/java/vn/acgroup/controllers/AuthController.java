package vn.acgroup.controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.config.SecurityTokenConfig;
import vn.acgroup.controllers.api.ChangePassRequest;
import vn.acgroup.controllers.api.LoginRequest;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.mail.MailService;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {

  @Autowired UserRepository userRepository;
  @Autowired MailService mailService;

  Logger log = Logger.getLogger(this.getClass().getName());

  @PostMapping(value = "/auth/login")
  @ResponseBody
  public ResponseEntity login(@RequestBody LoginRequest loginRequest, HttpServletResponse response)
      throws InterruptedException, ExecutionException {
    try {
      User user =
          userRepository
              .findByEmail(loginRequest.getEmail())
              .orElseThrow(
                  () -> new CustomException("User not found", HttpStatus.NOT_FOUND.value()));
      if (!user.getIsActive()) {
        mailService.userRegisterMail(user, user.getCode());
        return new ResponseEntity<>("Not active", HttpStatus.FORBIDDEN);
      } else {
        if (user.getPassword().equals(loginRequest.getPassword())) {

          Calendar cal = Calendar.getInstance(); // creates calendar
          cal.setTime(new Date()); // sets calendar time/date
          cal.add(Calendar.MONTH, 1); // adds one hour
          Date exp = cal.getTime(); // token will expire in 1 day

          Algorithm algorithm = Algorithm.HMAC256(SecurityTokenConfig.TOKEN_SECRET);
          String token =
              JWT.create()
                  .withJWTId(loginRequest.getEmail())
                  .withIssuer(SecurityTokenConfig.TOKEN_ISSUER)
                  .withClaim("group", user.getGroup())
                  .withExpiresAt(exp)
                  .sign(algorithm);
          Cookie cookie = new Cookie(SecurityTokenConfig.TOKEN_NAME, token);
          response.addCookie(cookie);

          return new ResponseEntity<>(token, HttpStatus.OK);
        }
        return new ResponseEntity<>("Wrong password", HttpStatus.FORBIDDEN);
      }
    } catch (Exception e) {
      return new ResponseEntity<>("Error " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping(value = "/auth/logout")
  @ResponseBody
  public String logout() throws InterruptedException, ExecutionException {

    return "No need to logout, just delete your token";
  }

  @PostMapping(value = "/auth/changepass")
  @ResponseBody
  @ApiOperation(
      value = "changePass",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity changePass(@RequestBody ChangePassRequest changePassRequest)
      throws InterruptedException, ExecutionException {
    User user = userRepository.findByEmailAndIsActive(changePassRequest.getEmail(), true).get();
    if (user != null) {
      if (user.getPassword().equals(changePassRequest.oldPassword())) {
        user.setPassword(changePassRequest.newPassword());
        userRepository.save(user);
        return new ResponseEntity<>("OK", HttpStatus.OK);
      }
      return new ResponseEntity<>("Wrong password", HttpStatus.FORBIDDEN);
    }
    return new ResponseEntity<>("User not found", HttpStatus.FORBIDDEN);
  }

  @GetMapping(value = "/auth/forgot_password/{email}")
  @ResponseBody
  @ApiOperation(value = "forgot_password")
  public ResponseEntity<String> forgot(@PathVariable String email)
      throws InterruptedException, ExecutionException {
    log.info("start with email: " + email);
    try {
      mailService.forgot(email);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }
}
