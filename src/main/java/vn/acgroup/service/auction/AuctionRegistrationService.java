package vn.acgroup.service.auction;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.acgroup.config.LightwalletConfig;
import vn.acgroup.controllers.api.WithdrawRequest;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.AuctionRegister;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.wallet.WalletService;

@Service
public class AuctionRegistrationService {

  Logger logger = Logger.getLogger(this.getClass().getName());

  @Autowired UserRepository userRepository;

  @Autowired AuctionRegisterRepository auctionRegisterRepository;

  @Autowired WalletService walletService;

  protected boolean valid(User user, Auction auction) throws CustomException {
    Optional<AuctionRegister> aucR =
        auctionRegisterRepository.findByUser_IdAndAuction_Id(user.getId(), auction.getId());
    if (aucR.isPresent() && !aucR.get().getIsDeleted())
      throw new CustomException("Bạn đã đăng ký đấu giá này.", HttpStatus.FORBIDDEN.value());
    if (auction.getStatus().equals("Ended"))
      throw new CustomException("Đấu giá đã kết thúc", HttpStatus.FORBIDDEN.value());
    return true;
  }

  public boolean isValidNormalRegister(User user, Auction auction) throws CustomException {
    return valid(user, auction);
  }

  public boolean isValidReverseRegister(User user, Auction auction) throws CustomException {
    boolean valid = valid(user, auction);
    if (user.getBidTurn() == 0)
      throw new CustomException("Hết lượt tham gia đấu giá", HttpStatus.FORBIDDEN.value());
    return valid;
  }

  public ResponseEntity<String> normalRegistration(
      User user, Auction auction, Optional<AuctionRegister> auctionRegister)
      throws CustomException {

    Optional<AuctionRegister> registeredOptional =
        auctionRegister.filter(
            (aucR) -> {
              return aucR.getIsDeleted().equals(true)
                  && user.getBonusBalance().compareTo(auction.getRegistrationFee()) >= 0;
            });
    if (registeredOptional.isPresent()) {

      logger.info("registeredOptional isPresent");
      AuctionRegister auc = registeredOptional.get();
      WithdrawRequest withdrawRequest =
          new WithdrawRequest(
              LightwalletConfig.ticketAddress,
              auction.getRegistrationFee(),
              "Phí đăng ký đấu giá " + auction.getId());
      try {
        walletService.sendBonus(user, withdrawRequest);
      } catch (Exception e) {
        walletService.resetBalance(user.getId());
        walletService.resetBonusBalance(user.getId());
        throw new CustomException(
            "Hệ thống đang bận. Vui lòng thử lại sau 10 giây!", HttpStatus.FORBIDDEN.value());
      }
      auc.setIsDeleted(false);
      auc.setTurn(auc.getTurn() + 1);
      userRepository.save(user);
      auctionRegisterRepository.save(auc);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } else {
      logger.info("registeredOptional is not Present");
      logger.info("user.getBonusBalance(): " + user.getBonusBalance());
      logger.info("auction.getRegistrationFee(): " + auction.getRegistrationFee());
      if (user.getBonusBalance().compareTo(auction.getRegistrationFee()) >= 0) {
        logger.info("user.getBonusBalance is bigger than fee: ");
        WithdrawRequest withdrawRequest =
            new WithdrawRequest(
                LightwalletConfig.ticketAddress,
                auction.getRegistrationFee(),
                "Phí đăng ký đấu giá " + auction.getId());
        try {
          logger.info("start send fee: ");
          walletService.sendBonus(user, withdrawRequest);
        } catch (Exception e) {
          logger.info("send fee ex: " + e.getMessage());
          if (e.getMessage().indexOf("Token amount is in insufficient") > -1) {
            walletService.resetBalance(user.getId());
            walletService.resetBonusBalance(user.getId());
            throw new CustomException(
                "Hệ thống đang bận. Vui lòng thử lại sau 10 giây!", HttpStatus.FORBIDDEN.value());
          }
          throw new CustomException(
              "Không đủ tiền đăng ký tham gia đấu giá", HttpStatus.FORBIDDEN.value());
        }
        AuctionRegister newAuctionRegister = new AuctionRegister(user, auction);
        userRepository.save(user);
        auctionRegisterRepository.save(newAuctionRegister);
      } else {
        logger.info("user.getBonusBalance is smaller than fee: ");
        throw new CustomException(
            "Không đủ tiền đăng ký tham gia đấu giá", HttpStatus.FORBIDDEN.value());
      }
      return new ResponseEntity<>("OK", HttpStatus.OK);
    }
  }

  public ResponseEntity<String> reverseRegistration(
      User user, Auction auction, Optional<AuctionRegister> auctionRegister)
      throws CustomException {
    Optional<AuctionRegister> registeredOptional =
        auctionRegister.filter(
            (aucR) -> {
              return aucR.getIsDeleted().equals(true);
            });
    if (registeredOptional.isPresent()) {
      registeredOptional.get().setIsDeleted(false);
      auctionRegisterRepository.save(registeredOptional.get());
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } else {
      AuctionRegister newAuctionRegister = new AuctionRegister(user, auction);
      auctionRegisterRepository.save(newAuctionRegister);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    }
  }
}
