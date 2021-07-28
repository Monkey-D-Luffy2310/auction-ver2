package vn.acgroup.controllers;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.giftcode.GiftcodeService;

@RestController
@CrossOrigin(origins = "*")
public class GiftcodeController {
  private final Logger log = LoggerFactory.getLogger(GiftcodeController.class);
  @Autowired UserRepository userRepository;
  @Autowired RestTemplate lightWalletRestTemplate;
  @Autowired GiftcodeService giftcodeService;

  @GetMapping(value = "/giftcode/enter/{giftcode}")
  @ResponseBody
  @ApiOperation(
      value = "giftcode/enter//{giftcode}",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity enter(@PathVariable String giftcode)
      throws InterruptedException, ExecutionException, CustomException, JSONException, IOException {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      User user =
          userRepository
              .findByEmailAndIsActive((String) principal, true)
              .orElseThrow(
                  () -> new CustomException("User not found", HttpStatus.NOT_FOUND.value()));
      String result = giftcodeService.enterGiftcode(user, giftcode);
      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "/giftcode/creategc/{id}")
  @ResponseBody
  @ApiOperation(
      value = "giftcode/creategc/{id}",
      authorizations = {@Authorization(value = "JWT")})
  public void create(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException, JSONException, IOException {
    giftcodeService.createGiftcode(id);
  }
}
