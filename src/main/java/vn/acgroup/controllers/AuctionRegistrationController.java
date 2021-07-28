package vn.acgroup.controllers;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.AuctionRegister;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.auction.AuctionRegistrationService;
import vn.acgroup.service.wallet.WalletService;
import vn.acgroup.service.mqtt.MqttService;

@Controller
@CrossOrigin(origins = "*")
public class AuctionRegistrationController {

  Logger logger = LoggerFactory.getLogger(BidController.class);

  @Autowired UserRepository userRepository;
  @Autowired AuctionRepository auctionRepository;
  @Autowired AuctionRegisterRepository auctionRegisterRepository;
  @Autowired WalletService walletService;
  @Autowired AuctionRegistrationService auctionRegistrationService;
  @Autowired MqttService mqttService;

  @PostMapping(value = "/auction_registration/{id}")
  @ResponseBody
  @ApiOperation(
      value = "register",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<String> auctionRegister(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user =
        userRepository
            .findByEmailAndIsActive((String) principal, true)
            .orElseThrow(
                () ->
                    new CustomException("Không tìm thấy người dùng", HttpStatus.NOT_FOUND.value()));
    logger.info("user to register auction: " + user.toString());
    Auction auction = auctionRepository.findById(id).get();
    logger.info(
        "auction to register auction: "
            + auction.getId()
            + " fee: "
            + auction.getRegistrationFee());
    Optional<AuctionRegister> auctionRegister =
        auctionRegisterRepository.findByUserAndAuction(user, auction);

    switch (auction.getType()) {
      case "Normal":
        if (auctionRegistrationService.isValidNormalRegister(user, auction)) {
          auctionRegistrationService.normalRegistration(user, auction, auctionRegister);

          HashMap<String, String> fields = new HashMap<String, String>();
          Optional<Iterable<AuctionRegister>> auctionRegister_ =
              auctionRegisterRepository.findByAuction_IdAndIsDeleted(auction.getId(), false);
          auctionRegister_.ifPresent(
              rgs -> {
                int counter = 0;

                for (AuctionRegister rg : rgs) {
                  counter++;
                }
                fields.put("registed", String.valueOf(counter));
                fields.put("auction", String.valueOf(auction.getId()));
                fields.put("username", user.getName());
                fields.put("userid", String.valueOf(user.getId()));
                fields.put(
                    "created",
                    auctionRegisterRepository
                        .findByUserAndAuction(user, auction)
                        .get()
                        .getCreated()
                        .toString());
                try {
                  mqttService.sendToMqtt(fields, "auction/registration");
                } catch (JsonProcessingException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              });
          System.out.print(fields);
        }
        ;
        break;
      case "Reverse":
        if (auctionRegistrationService.isValidReverseRegister(user, auction))
          auctionRegistrationService.reverseRegistration(user, auction, auctionRegister);
        break;
      default:
        break;
    }
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  @DeleteMapping(value = "/auction_registration/{id}")
  @ResponseBody
  @ApiOperation(
      value = "deleteRegistration",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<String> delete(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByEmailAndIsActive((String) principal, true).get();
    Auction auction = auctionRepository.findById(id).get();
    AuctionRegister auctionRegister =
        auctionRegisterRepository
            .findByUserAndAuction(user, auction)
            .orElseThrow(
                () ->
                    new CustomException(
                        "Quý khách chưa đăng ký đấu giá này", HttpStatus.FORBIDDEN.value()));
    auctionRegister.setIsDeleted(true);
    userRepository.save(user);
    auctionRegisterRepository.save(auctionRegister);

    HashMap<String, String> fields = new HashMap<String, String>();
    Optional<Iterable<AuctionRegister>> auctionRegister_ =
        auctionRegisterRepository.findByAuction_IdAndIsDeleted(id, false);
    auctionRegister_.ifPresent(
        rgs -> {
          int counter = 0;
          for (AuctionRegister rg : rgs) {
            counter++;
          }
          fields.put("registed", String.valueOf(counter));
          fields.put("auction", String.valueOf(id));
          fields.put("username", user.getName());
          fields.put("userid", String.valueOf(user.getId()));
          try {
            mqttService.sendToMqtt(fields, "auction/cancelRegistration");
          } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        });
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  @GetMapping(value = "/auction_registration/all")
  @ResponseBody
  @ApiOperation(value = "getAllRegistration")
  public Iterable<AuctionRegister> getAllAuctionRegister()
      throws InterruptedException, ExecutionException {
    return auctionRegisterRepository.findByIsDeleted(false);
  }

  @GetMapping(value = "/auction_registrations/user/{id}")
  @ResponseBody
  @ApiOperation(value = "getRegistedAuctionsOfUser")
  public ResponseEntity getAllAuctionByUSerId(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException {
    User user = userRepository.findById(id).get();
    Iterable<AuctionRegister> registrations =
        auctionRegisterRepository
            .findByUser(user)
            .orElseThrow(
                () ->
                    new CustomException(
                        "Quý khách không đăng ký đấu giá nào", HttpStatus.FORBIDDEN.value()));
    return new ResponseEntity<>(registrations, HttpStatus.OK);
  }

  @GetMapping(value = "/auction_registrations/auction/{id}")
  @ResponseBody
  @ApiOperation(value = "getUsersRegistedAuction")
  public ResponseEntity getAllUserByAuctionId(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException {
    Auction auction = auctionRepository.findById(id).get();
    return new ResponseEntity<>(
        auctionRegisterRepository
            .findByAuctionAndIsDeleted(auction, false)
            .orElseThrow(
                () -> new CustomException("Chưa có ai đăng ký.", HttpStatus.FORBIDDEN.value())),
        HttpStatus.OK);
  }

  @GetMapping(value = "/auction_registrations/biddingAuction/user/{id}")
  @ResponseBody
  @ApiOperation(value = "get bidding auction of user")
  public Iterable<AuctionRegister> getBiddingAuction(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException {
    return auctionRegisterRepository.findBiddingAuction(id);
  }
}
