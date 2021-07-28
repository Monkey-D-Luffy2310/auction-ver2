package vn.acgroup.controllers;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.controllers.api.AutoBidRegisterRequest;
import vn.acgroup.entities.AutoBid;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.AutoBidRepository;
import vn.acgroup.repositories.UserRepository;

@RestController
@CrossOrigin(origins = "*")
public class AutoBidController {
  @Autowired UserRepository userRepository;
  @Autowired AutoBidRepository autoBidRepository;

  @PostMapping("/autobid/register") // bid request
  @ResponseBody
  @ApiOperation(
      value = "/autobid/register",
      authorizations = {@Authorization(value = "JWT")})
  public void autoBidRegister(@RequestBody AutoBidRegisterRequest autoRequest)
      throws InterruptedException, ExecutionException, CustomException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user =
        userRepository
            .findByEmailAndIsActive((String) principal, true)
            .orElseThrow(() -> new CustomException("User not found", 404));
    AutoBid autoBid = new AutoBid();
    autoBid.setUser(user);
    autoBid.setAuctionId(autoRequest.getAuction_id());
    autoBid.setMaxBid(autoRequest.getMaxBid());
    autoBid.setActive(true);
    autoBidRepository.save(autoBid);
  }

  @PutMapping("/autobid/delete/{id}") // bid request
  @ResponseBody
  @ApiOperation(
      value = "/autobid/delete",
      authorizations = {@Authorization(value = "JWT")})
  public void deleteAutoBid(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user =
        userRepository
            .findByEmailAndIsActive((String) principal, true)
            .orElseThrow(() -> new CustomException("User not found", 404));
    Optional<AutoBid> autoBid = autoBidRepository.findById(id);
    autoBid
        .filter(
            (a) -> {
              return a.getUser().equals(user);
            })
        .ifPresent(
            (a) -> {
              a.setActive(false);
            });
    autoBidRepository.save(autoBid.get());
  }
}
