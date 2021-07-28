package vn.acgroup.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.controllers.api.BankAccountRequest;
import vn.acgroup.controllers.api.WithdrawRequest;
import vn.acgroup.controllers.api.WithdrawToBankRequest;
import vn.acgroup.controllers.lightwallet.Notification;
import vn.acgroup.controllers.vndt.AddressResponse;
import vn.acgroup.controllers.vndt.Deposit;
import vn.acgroup.entities.SavedBank;
import vn.acgroup.entities.Transaction;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.TransactionRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.giftcode.GiftcodeService;
import vn.acgroup.service.mail.MailService;
import vn.acgroup.service.wallet.WalletService;

@RestController
@CrossOrigin(origins = "*")
public class WalletController {

  @Autowired UserRepository userRepository;
  @Autowired TransactionRepository transactionRepository;
  @Autowired RestTemplate lightWalletRestTemplate;
  @Autowired WalletService walletService;
  @Autowired MailService mailService;
  @Autowired GiftcodeService giftcodeService;

  @Autowired
  @Qualifier("bonusWalletTemplate")
  RestTemplate bonusWalletTemplate;

  Logger logger = Logger.getLogger(this.getClass().getName());

  @PostMapping(value = "wallet/withdraw")
  @ResponseBody
  @ApiOperation(
      value = "/wallet/withdraw",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<String> withdraw(@RequestBody WithdrawRequest withdrawRequest)
      throws InterruptedException, ExecutionException {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      User user =
          userRepository
              .findByEmailAndIsActive((String) principal, true)
              .orElseThrow(() -> new CustomException("User not found", 404));
      if (walletService.sendVNDT(user, withdrawRequest) == 200)
        return new ResponseEntity<>("OK", HttpStatus.OK);
      else return new ResponseEntity<>("Failed", HttpStatus.FORBIDDEN);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping(value = "wallet/withdraw-to-bank")
  @ResponseBody
  @ApiOperation(
      value = "withdraw money to bank",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<String> withdrawToBank(@RequestBody WithdrawToBankRequest withdrawRequest)
      throws InterruptedException, Exception {

    if (withdrawRequest.getAmount().compareTo(new BigDecimal(50000)) < 0) {
      throw new CustomException("Min amount is 50,000 VNDT", 404);
    }
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      User user =
          userRepository
              .findByEmailAndIsActive((String) principal, true)
              .orElseThrow(() -> new CustomException("User not found", 404));
      if (walletService.withdrawVndtToBank(user, withdrawRequest) == 200)
        return new ResponseEntity<>("OK", HttpStatus.OK);
      else return new ResponseEntity<>("Failed", HttpStatus.FORBIDDEN);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping(value = "wallet/callback/admin/123456/update_transaction")
  @ResponseBody
  @ApiOperation(value = "cập nhật transaction ví product")
  public ResponseEntity<String> update(@RequestBody Notification notifi)
      throws InterruptedException, ExecutionException {
    try {
      walletService.updateTransaction(notifi);
      return new ResponseEntity<>("Save transaction", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping(value = "wallet/callback/admin/123456/update/bonus")
  @ResponseBody
  @ApiOperation(value = "cập nhật transaction ví bonus")
  public ResponseEntity<String> updateTransactionBonus(@RequestBody Notification notifi)
      throws InterruptedException, ExecutionException {
    try {
      walletService.updateTransactionBonus(notifi);
      return new ResponseEntity<>("Save transaction", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping(value = "wallet/transaction")
  @ResponseBody
  @ApiOperation(
      value = "/wallet/transaction",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity transaction() throws InterruptedException, ExecutionException {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
      Long user_id = optional.get().getId();
      return new ResponseEntity<>(
          transactionRepository.findByFromUserOrToUserOrderByCreatedDesc(user_id, user_id),
          HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error" + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "wallet/transaction/deposit")
  @ResponseBody
  @ApiOperation(
      value = "",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity deposit() throws InterruptedException, ExecutionException {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Optional<User> user = userRepository.findByEmailAndIsActive((String) principal, true);
      return new ResponseEntity<>(
          transactionRepository.findByToUserOrderByCreatedDesc(user.get().getId()), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping(value = "wallet/bank/account-info")
  @ResponseBody
  @ApiOperation(
      value = "get account info by number and bank",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<?> getBankAccountInfo(@RequestBody BankAccountRequest request)
      throws InterruptedException, ExecutionException {
    try {
      return new ResponseEntity<>(
          walletService.getBankAccountInfo(request.getAccountNumber(), request.getBankCode()),
          HttpStatus.OK);
    } catch (Exception e) {
      logger.warning("get bank ex: " + e.getMessage());
      return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "wallet/deposit-bank")
  @ResponseBody
  @ApiOperation(
      value = "get deposit bank info",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<?> getDepositBank() throws InterruptedException, ExecutionException {
    try {
      List<SavedBank> banks = walletService.getDepositBank();
      System.out.println(new Gson().toJson(banks));
      return new ResponseEntity<>(banks, HttpStatus.OK);
    } catch (Exception e) {
      logger.warning("get bank ex: " + e.getMessage());
      return null;
    }
  }

  @GetMapping(value = "wallet/transaction/withdraw")
  @ResponseBody
  @ApiOperation(
      value = "/wallet/transaction/withdraw",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<?> withdraw() throws InterruptedException, ExecutionException {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Optional<User> user = userRepository.findByEmailAndIsActive((String) principal, true);
      return new ResponseEntity<>(
          transactionRepository.findByFromUserOrderByCreatedDesc(user.get().getId()),
          HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping(value = "wallet/callback/deposit/banking")
  @ResponseBody
  @ApiOperation(value = "wallet/callback/deposit/banking")
  public AddressResponse test(@RequestBody Deposit deposit)
      throws InterruptedException, ExecutionException {
    String vndt_id = deposit.getData().getId();
    // Vndt_id(Long.toHexString(user.getId()+1000));
    Long user_id = Long.parseLong(vndt_id, 16) - 1000;
    AddressResponse addressResponse = new AddressResponse();
    addressResponse.setAddress(userRepository.findById(user_id).get().getWalletAddress());
    return addressResponse;
  }

  @GetMapping(value = "/wallet/resetBalance/{id}")
  @ResponseBody
  @ApiOperation(
      value = "reset balance",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity resetBalance(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException, JSONException, IOException {
    // userRepository.findAll().forEach((user) ->
    // walletService.resetBalance(user.getId()));
    // return "OK";
    if (walletService.resetBalance(id).equals("OK"))
      return new ResponseEntity<>("OK", HttpStatus.OK);
    else {
      return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "/wallet/resetBonusBalance/{user_id}")
  @ResponseBody
  @ApiOperation(
      value = "reset bonus balance",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity resetBonusBalance(@PathVariable long user_id)
      throws InterruptedException, ExecutionException, CustomException, JSONException, IOException {
    // userRepository.findAll().forEach((user) ->
    // walletService.resetBalance(user.getId()));
    // return "OK";
    if (walletService.resetBonusBalance(user_id).equals("OK"))
      return new ResponseEntity<>("OK", HttpStatus.OK);
    else {
      return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "/wallet/createBonusAddress/{id}")
  @ResponseBody
  @ApiOperation(
      value = "reset balance",
      authorizations = {@Authorization(value = "JWT")})
  public String createBonus(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException, JSONException, IOException {
    User user = userRepository.findById(id).get();
    walletService.createBonusWallet(user.getId());
    return "OK";
  }

  @GetMapping(value = "/wallet/transfer/{amount}")
  @ResponseBody
  @ApiOperation(
      value = "transfer",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity transfer(@PathVariable String amount)
      throws InterruptedException, ExecutionException, CustomException, JSONException, IOException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    amount = amount.replaceAll(",", "");
    User user =
        userRepository
            .findByEmailAndIsActive((String) principal, true)
            .orElseThrow(() -> new CustomException("User not found", 404));
    if (walletService.transfer(user, new BigDecimal(amount)) == 200)
      return new ResponseEntity<>("OK", HttpStatus.OK);
    else return new ResponseEntity<>("Error", HttpStatus.FORBIDDEN);
  }

  @GetMapping(value = "/wallet/createProductWallet/{id}")
  @ResponseBody
  @ApiOperation(
      value = "create Product Wallet",
      authorizations = {@Authorization(value = "JWT")})
  public void createProductWallet(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException, JSONException, IOException {
    walletService.createProductWallet(id);
  }

  @GetMapping(value = "/wallet/activeWallet/{id}")
  @ResponseBody
  @ApiOperation(
      value = "send 0.1 trx",
      authorizations = {@Authorization(value = "JWT")})
  public void activeWallet(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException, JSONException, IOException {
    User user = userRepository.findById(id).get();
    walletService.activeWallet(user.getWalletAddress());
    walletService.activeWallet(user.getBonusAddress());
  }

  @GetMapping(value = "/wallet/transactions")
  @ResponseBody
  @ApiOperation(
      value = "get all transaction",
      authorizations = {@Authorization(value = "JWT")})
  public Iterable<Transaction> transactions()
      throws InterruptedException, ExecutionException, CustomException, JSONException, IOException {
    return transactionRepository.findAll();
  }

  @PostMapping(value = "wallet/sendBonus")
  @ResponseBody
  @ApiOperation(
      value = "/wallet/sendBonus",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity<String> sendBonus(@RequestBody WithdrawRequest withdrawRequest)
      throws InterruptedException, ExecutionException {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      User user =
          userRepository
              .findByEmailAndIsActive((String) principal, true)
              .orElseThrow(() -> new CustomException("User not found", 404));
      walletService.sendBonus(user, withdrawRequest);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed" + e.getMessage(), HttpStatus.EXPECTATION_FAILED);
    }
  }

  @GetMapping(value = "/wallet/test")
  @ResponseBody
  @ApiOperation(
      value = "test",
      authorizations = {@Authorization(value = "JWT")})
  public void test()
      throws InterruptedException, ExecutionException, CustomException, JSONException,
          IOException {}
}
