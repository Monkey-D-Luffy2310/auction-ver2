package vn.acgroup.controllers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import net.bytebuddy.utility.RandomString;
import vn.acgroup.config.LightwalletConfig;
import vn.acgroup.controllers.api.EditUserInformation;
import vn.acgroup.controllers.api.RegisterRequest;
import vn.acgroup.controllers.lightwallet.Account;
import vn.acgroup.controllers.lightwallet.AccountResponse;
import vn.acgroup.entities.Asset;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.User;
import vn.acgroup.repositories.AddressRepository;
import vn.acgroup.repositories.AssetRepository;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.mail.MailService;
import vn.acgroup.service.wallet.WalletService;
import vn.acgroup.utils.Utils;
import vn.acgroup.repositories.AuctionRepository;

@CrossOrigin(origins = "*")
@RestController
public class UserController {

  @Autowired AssetRepository assetRepository;
  @Autowired AuctionRepository auctionRepository;
  @Autowired UserRepository userRepository;
  @Autowired RestTemplate lightWalletRestTemplate;
  @Autowired AddressRepository addressRepository;
  @Autowired MailService mailService;
  @Autowired AuctionRegisterRepository auctionRegisterRepository;
  @Autowired WalletService walletService;

  @Autowired
  @Qualifier("bonusWalletTemplate")
  RestTemplate bonusWalletTemplate;

  Logger log = Logger.getLogger(this.getClass().getName());

  @GetMapping(value = "/user/me")
  @ResponseBody
  @ApiOperation(
      value = "/user/me",
      authorizations = {@Authorization(value = "JWT")})
  public Optional<User> me() throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userRepository.findByEmailAndIsActive((String) principal, true);
  }

  @GetMapping(value = "/user/f1-list")
  @ResponseBody
  @ApiOperation(
      value = "/user/f1-list",
      authorizations = {@Authorization(value = "JWT")})
  public List<User> f1List() throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByEmailAndIsActive((String) principal, true).get();
    return userRepository.findBySponsorOrderByCreatedDesc(user.getId());
  }

  @PostMapping(value = "/user/register")
  @ResponseBody
  public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest)
      throws InterruptedException, ExecutionException {

    Optional<User> userActiveOptional =
        userRepository.findByEmailAndIsActive(registerRequest.getEmail(), true);
    if (userActiveOptional.isPresent())
      return new ResponseEntity<>(
          " Account with email : " + registerRequest.getEmail() + " is already exist",
          HttpStatus.BAD_REQUEST);

    Optional<User> userOptional =
        userRepository.findByEmailAndIsActive(registerRequest.getEmail(), false);
    if (userOptional.isPresent() && !userOptional.get().getCode().isEmpty()) {
      mailService.userRegisterMail(userOptional.get(), userOptional.get().getCode());
      return new ResponseEntity<>(
          "Account with email : "
              + registerRequest.getEmail()
              + " is already registed. Please check your mail to verify",
          HttpStatus.BAD_REQUEST);
    }

    long sponsorId = 69505;
    if (!registerRequest.getSponsor().equals("") && registerRequest.getSponsor() != null) {
      sponsorId = Utils.hexToDecimal(registerRequest.getSponsor());
      Optional<User> sponsorOptional = userRepository.findById(sponsorId);
      if (!sponsorOptional.isPresent())
        return new ResponseEntity<>(
            " Sponsor " + registerRequest.getSponsor() + " not found ", HttpStatus.BAD_REQUEST);
    }

    User user = new User(registerRequest);
    String code = RandomString.make(12);
    user.setCode(code);
    user.setSponsor(sponsorId);
    user.setMobile(registerRequest.getMobile());
    mailService.userRegisterMail(user, code);
    userRepository.save(user);

    Account account = new Account();
    account.setName(user.getId() + "");
    account.setCurrency("TRX");
    account.setPrimary(true);
    HttpEntity<Account> request = new HttpEntity<>(account);
    AccountResponse result =
        lightWalletRestTemplate.postForObject(
            LightwalletConfig.URL, request, AccountResponse.class);
    user.setWalletAddress(result.getPrimaryAddress());
    walletService.activeWallet(result.getPrimaryAddress());
    result =
        bonusWalletTemplate.postForObject(LightwalletConfig.URL, request, AccountResponse.class);
    user.setBonusAddress(result.getPrimaryAddress());
    userRepository.save(user);
    walletService.activeWallet(result.getPrimaryAddress());
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  @GetMapping(value = "/user/reVerify/{email}")
  @ResponseBody
  @ApiOperation(
      value = "/user/reVerify",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity reVerify(@PathVariable String email)
      throws InterruptedException, ExecutionException {
    userRepository
        .findByEmailAndIsActive(email, false)
        .ifPresent(
            (user) -> {
              mailService.userRegisterMail(user, user.getCode());
            });
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  // TODO ONLY ADMIN can call this method
  @GetMapping(value = "/user")
  @ResponseBody
  @ApiOperation(
      value = "/user",
      authorizations = {@Authorization(value = "JWT")})
  public Iterable<User> getAllUser() throws InterruptedException, ExecutionException {
    return userRepository.findAll();
  }

  // TODO ONLY ADMIN can call this method
  @GetMapping(value = "/user/email/{email}")
  @ResponseBody
  public Optional<User> byEmailAndIsActive(@PathVariable String email)
      throws InterruptedException, ExecutionException {
    try {
      // xóa rồi đki lại-> 2 email trong userRepo
      return userRepository.findByEmailAndIsActive(email, true);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  // TODO ONLY ADMIN can call this method
  @GetMapping(value = "/user/{id}")
  @ResponseBody
  public Optional<User> getUser(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    try {
      return userRepository.findById(id);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  // TODO ONLY ADMIN can call this method
  @DeleteMapping(value = "/user/{id}")
  @ApiOperation(
      value = "delete",
      authorizations = {@Authorization(value = "JWT")})
  @ResponseBody
  public ResponseEntity<String> delete(@PathVariable long id)
      throws InterruptedException, ExecutionException {

    Optional<User> optional = userRepository.findById(id);

    if (!optional.isPresent() || !optional.get().getIsActive())
      return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    else {
      User user = userRepository.findById(id).get();
      user.setIsActive(false);
      user.setCode(null);
      user.setResetToken(null);
      userRepository.save(user);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    }
  }

  @PutMapping(value = "/user/edit/{id}")
  @ResponseBody
  @ApiOperation(
      value = "edit",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity edit(
      @PathVariable long id, @RequestBody EditUserInformation editUserInformation)
      throws InterruptedException, ExecutionException {
    try {
      User user = userRepository.findById(id).get();
      user.edit(editUserInformation);
      userRepository.save(user);
      Iterable<Auction> auction = auctionRepository.findByUser_Id(id);
      auction.forEach(
          _auction -> {
            Asset asset = assetRepository.findAssetByAuctions_Id(_auction.getId());
            RediSearch.getData(_auction, user, asset, userRepository, RediSearch.client);
          });
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error :" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }
}
