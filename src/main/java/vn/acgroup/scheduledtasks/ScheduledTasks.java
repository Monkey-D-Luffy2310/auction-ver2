package vn.acgroup.scheduledtasks;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import vn.acgroup.config.LightwalletConfig;
import vn.acgroup.controllers.RediSearch;
import vn.acgroup.controllers.api.WithdrawRequest;
import vn.acgroup.entities.Bid;
import vn.acgroup.entities.User;
import vn.acgroup.repositories.AssetRepository;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.BidRepository;
import vn.acgroup.repositories.LikedAuctionRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.auction.AuctionService;
import vn.acgroup.service.mail.MailService;
import vn.acgroup.service.mqtt.MqttService;
import vn.acgroup.service.wallet.WalletService;

@Service
public class ScheduledTasks {

  Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

  @Autowired AuctionRepository auctionRepository;

  @Autowired LikedAuctionRepository likedAuctionRepository;

  @Autowired AssetRepository assetRepository;

  @Autowired MailService mailService;

  @Autowired UserRepository userRepository;

  @Autowired BidRepository bidRepository;

  @Autowired AuctionRegisterRepository auctionRegisterRepository;

  @Autowired AuctionService auctionService;

  @Autowired MqttService mqttService;

  @Autowired WalletService walletService;

  @Scheduled(cron = "* * * ? * *")
  public void activeAuction() {
    HashMap<String, String> status = new HashMap<String, String>();
    status.put("status", "");
    status.put("auction_id", "");
    auctionRepository
        .findByStatus("Upcoming")
        .forEach(
            auc -> {
              LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
              LocalDateTime start = auc.getStartAt();
              LocalDateTime end = auc.getEndAt();
              User admin = userRepository.findById(LightwalletConfig.adminId).get();
              if (now.isAfter(start) && now.isBefore(end)) {
                int userRegisted = auctionRegisterRepository.findAttendingUser(auc.getId());
                log.info(
                    "user registed : " + userRegisted + " - min user: " + auc.getAttendingUser());
                if (userRegisted - auc.getAttendingUser() >= 0) {
                  auc.setStatus("Active");
                  auctionRepository.save(auc);
                  try {
                    status.put("status", "Active");
                    status.replace("auction_id", auc.getId() + "");
                    mqttService.sendToMqtt(status, "auction/status");
                  } catch (JsonProcessingException e) {
                    e.printStackTrace();
                  }
                } else {
                  auc.setStatus("Ended");
                  auctionRepository.save(auc);
                  try {
                    status.put("status", "Ended");
                    status.replace("auction_id", auc.getId() + "");
                    mqttService.sendToMqtt(status, "auction/status");
                    BigDecimal quantity = auc.getRegistrationFee();
                    auctionRegisterRepository
                        .findByAuction_IdAndIsDeleted(auc.getId(), false)
                        .ifPresent(
                            res -> {
                              res.forEach(
                                  reg -> {
                                    WithdrawRequest withdrawRequest =
                                        new WithdrawRequest(
                                            reg.getUser().getBonusAddress(),
                                            quantity,
                                            "Hoàn phí đăng ký đấu giá " + auc.getId());
                                    walletService.sendBonus(admin, withdrawRequest);
                                  });
                            });

                  } catch (JsonProcessingException e) {
                    e.printStackTrace();
                  }
                }
              }
            });
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void schedule() {
    likedAuctionRepository
        .findByIsDelete(false)
        .forEach(
            el -> {
              long time =
                  ChronoUnit.DAYS.between(
                      el.getAuction().getStartAt(),
                      LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
              if (time >= 1 && time <= 3) {
                mailService.upcomingAuctionMail(el, time);
              }
            });
  }

  // @Scheduled(cron = "* * * ? * *")
  public void autoBidToWin() {
    BigDecimal addition = BigDecimal.valueOf(200);
    User admin = userRepository.findById(Long.valueOf(1004)).get();
    auctionRepository
        .findByType("Reverse")
        .forEach(
            auc -> {
              // long time =ChronoUnit.SECONDS.between(auc.getEndAt(),
              // LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
              Float time = auc.getTimeLeft();
              if (time <= 0.1 && time > 0) {
                Bid bid = new Bid(admin, auc);
                bid.setBidPrice(auc.getCurrentPrice().add(addition));
                bidRepository.save(bid);
              }
            });
  }

  @Scheduled(cron = "* * * ? * *")
  public void endAuction() {
    HashMap<String, String> status = new HashMap<String, String>();
    status.put("status", "Ended");
    status.put("auction_id", "");
    status.put("userId", "");
    auctionRepository
        .findByTypeAndStatus("Normal", "Active")
        .forEach(
            auction -> {
              Optional<Bid> top1 =
                  bidRepository.findTop1ByAuction_idAndStatusOrderByCreatedDesc(
                      auction.getId(), "true");
              if (auction.getEndAt().isBefore(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                  || (top1.isPresent()
                      && ChronoUnit.MINUTES.between(
                              top1.get().getCreated(),
                              LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")))
                          > 60)) {
                Optional<User> winner;
                try {
                  log.info("start end aution : " + auction.getId());
                  winner = auctionService.endAuction(auction);
                  if (winner.isPresent()) {
                    User winU = winner.get();
                    HashMap<String, String> val = new HashMap<String, String>();
                    val.put("userId", winU.getId() + "");
                    val.put("auction_id", auction.getId() + "");
                    val.put("email", winU.getEmail());
                    val.put("username", winU.getName());
                    val.put("price", top1.get().getBidPrice() + "");
                    val.put("time", top1.get().getUpdated() + "");
                    val.put("auction_name", auction.getAssest().getName());
                    val.put("avatar", winner.get().getAvatar());
                    status.replace("auction_id", auction.getId() + "");
                    status.replace("userId", winU.getId() + "");
                    mqttService.sendToMqtt(status, "auction/status");
                    mqttService.sendToMqtt(val, "auction/win");
                  }
                } catch (Exception e) {
                  log.info(e.getMessage());
                  e.printStackTrace();
                }
                RediSearch.client.deleteDocument(String.valueOf(auction.getId()));
              }
            });
  }

  @Scheduled(cron = "0 * * * * *")
  public void checkAuctionExpires() throws Exception {
    System.out.println("start check auction expires at " + LocalDateTime.now().toString());
    auctionService.checkAuctionExpires();
  }
}
