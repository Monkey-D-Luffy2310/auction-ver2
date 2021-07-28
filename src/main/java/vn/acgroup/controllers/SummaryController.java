package vn.acgroup.controllers;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.config.LightwalletConfig;
import vn.acgroup.controllers.api.WithdrawRequest;
import vn.acgroup.entities.User;
import vn.acgroup.repositories.AddressRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.wallet.WalletService;

@RestController
public class SummaryController {

  @Autowired UserRepository userRepository;
  @Autowired AddressRepository addressRepository;

  @Autowired WalletService walletService;

  static String DAUGIAACG_ADDRESS = "TDxGHghexsmKr67uwAtKCfcpsd8eVi5nr1";

  @GetMapping(value = "/summary/daily-accounting")
  @ResponseBody
  @ApiOperation(
      value = "daily accounting",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity dailyAccounting(@RequestParam("key") String keyPass)
      throws InterruptedException, ExecutionException {
    if (!keyPass.equals("adminAuction")) {
      return new ResponseEntity<>("Key invalid!", HttpStatus.FORBIDDEN);
    }
    User ticketAdmin = userRepository.findById(LightwalletConfig.adminId).get();

    if (ticketAdmin.getBonusBalance().floatValue() > 0) {
      walletService.sendBonus(
          ticketAdmin,
          new WithdrawRequest(DAUGIAACG_ADDRESS, ticketAdmin.getBonusBalance(), "ticket amount"));
    }
    if (ticketAdmin.getVNDTBalance().floatValue() > 0) {
      walletService.sendVNDT(
          ticketAdmin,
          new WithdrawRequest(DAUGIAACG_ADDRESS, ticketAdmin.getVNDTBalance(), "ticket amount"));
    }

    User productAdmin = userRepository.findById(LightwalletConfig.adminProduct).get();
    if (productAdmin.getBonusBalance().floatValue() > 0) {
      walletService.sendBonus(
          productAdmin,
          new WithdrawRequest(DAUGIAACG_ADDRESS, productAdmin.getBonusBalance(), "ticket amount"));
    }
    if (productAdmin.getVNDTBalance().floatValue() > 0) {
      walletService.sendVNDT(
          productAdmin,
          new WithdrawRequest(DAUGIAACG_ADDRESS, productAdmin.getVNDTBalance(), "ticket amount"));
    }
    return new ResponseEntity<>("DONE", HttpStatus.OK);
  }
}
