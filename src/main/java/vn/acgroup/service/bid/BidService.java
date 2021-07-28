package vn.acgroup.service.bid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import vn.acgroup.controllers.BidController;
import vn.acgroup.controllers.api.BidRequest;
import vn.acgroup.entities.Asset;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.AuctionRegister;
import vn.acgroup.entities.Bid;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.AssetRepository;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.BidRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.mqtt.MqttService;
import vn.acgroup.service.wallet.WalletService;

@Service
public class BidService {
  Logger logger = LoggerFactory.getLogger(BidController.class);
  @Autowired UserRepository userRepository;

  @Autowired AssetRepository assetRepository;

  @Autowired BidRepository bidRepository;

  @Autowired AuctionRepository auctionRepository;

  @Autowired RestTemplate emqxClient;

  @Autowired MqttService mqttService;

  @Autowired AuctionRegisterRepository auctionRegisterRepository;

  @Autowired WalletService walletService;

  //  boolean dangdau = true;
  //  long timestartrequest => thoiwf gian bat dau request;

  protected boolean valid(
      User user, Asset asset, Optional<Auction> auctionOptional, @RequestBody BidRequest bidRequest)
      throws CustomException {
    Auction auction = auctionOptional.get();
    logger.info("auction To check: " + auction.getId() + " fee: " + auction.getRegistrationFee());
    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    if (!userRepository.existsById(user.getId()))
      throw new CustomException("Không tìm thấy người dùng.", HttpStatus.FORBIDDEN.value());
    if (!auctionOptional.isPresent() || auctionOptional.get().getStatus() == "đã xoá")
      throw new CustomException("Không tìm thấy đấu giá", HttpStatus.NOT_FOUND.value());
    if (!auctionOptional.get().getStatus().equals("Active"))
      throw new CustomException("Đấu giá chưa diễn ra", HttpStatus.FORBIDDEN.value());
    if (!(auctionRegisterRepository.findByUserAndAuctionAndIsDeleted(
            user, auctionOptional.get(), false))
        .isPresent())
      throw new CustomException("Chưa đăng kí đấu giá", HttpStatus.FORBIDDEN.value());
    if (now.isAfter(auctionOptional.get().getEndAt()))
      throw new CustomException("Đấu giá đã kết thúc", HttpStatus.FORBIDDEN.value());
    logger.info(
        "user.getVNDTBalance(): "
            + user.getVNDTBalance().longValue()
            + "  - auction getWarranty()"
            + auctionOptional.get().getWarranty().longValue());
    if (user.getVNDTBalance().compareTo(auctionOptional.get().getWarranty()) < 0)
      throw new CustomException("Không đủ tiền đặt cọc đấu giá", HttpStatus.FORBIDDEN.value());
    // Long asset_id = auctionOptional.get().getAssest().getId();
    //    BigDecimal currentPrice = asset.getCurrentPrice();
    BigDecimal currentPrice = auctionOptional.get().getCurrentPrice();
    BigDecimal bidPrice = bidRequest.getBidPrice();
    BigDecimal aucBidPrice = auctionOptional.get().getBidPrice();

    if (currentPrice.compareTo(aucBidPrice) >= 0) {
      if (bidPrice.compareTo(currentPrice) < 1)
        throw new CustomException("Giá đấu phải cao hơn " + currentPrice, HttpStatus.OK.value());
    } else {
      if (bidPrice.compareTo(aucBidPrice) < 0)
        throw new CustomException(
            "Giá đấu không được thấp hơn " + aucBidPrice, HttpStatus.OK.value());
    }

    if (auctionOptional.get().getCurrentWinner() == user.getId())
      throw new CustomException(
          "Bạn là người thắng đấu giá hiện tại, không thể đấu giá tiếp",
          HttpStatus.FORBIDDEN.value());
    return true;
  }

  public boolean isValidNormalBid(
      User user, Asset asset, Optional<Auction> auctionOptional, @RequestBody BidRequest bidRequest)
      throws CustomException {
    return valid(user, asset, auctionOptional, bidRequest);
  }

  public boolean isValidReverserBid(
      User user, Asset asset, Optional<Auction> auctionOptional, @RequestBody BidRequest bidRequest)
      throws CustomException {
    boolean valid = valid(user, asset, auctionOptional, bidRequest);
    if (user.getBidTurn() == 0)
      throw new CustomException("Đã hết lượt đấu giá", HttpStatus.FORBIDDEN.value());
    return valid;
  }

  public void bid(User user, Asset asset, Auction auction, @RequestBody BidRequest bidRequest)
      throws JsonProcessingException, CustomException {

    AuctionRegister auctionRegister =
        auctionRegisterRepository.findByUser_IdAndAuction_Id(user.getId(), auction.getId()).get();
    //    BigDecimal nowWarranty =
    //       auction.getBuyPrice()
    //            .multiply(new BigDecimal(auction.getPercent()).divide(new BigDecimal(100)));
    BigDecimal preWarranty = auction.getWarranty();
    if (user.getVNDTBalance().compareTo(preWarranty) < 0)
      throw new CustomException(
          "Quý khách vui lòng nạp thêm tối thiểu "
              + preWarranty.subtract(user.getVNDTBalance())
              + " VNDT",
          HttpStatus.FORBIDDEN.value());
    else {
      Optional<User> currenWinner = userRepository.findById(auction.getCurrentWinner());

      currenWinner.ifPresent(
          curW -> {
            curW.unfreeze(auction.getWarranty());
            AuctionRegister regisOfCurrentWinner =
                auctionRegisterRepository
                    .findByUser_IdAndAuction_Id(currenWinner.get().getId(), auction.getId())
                    .get();
            regisOfCurrentWinner.setDepositing(false);
            auctionRegisterRepository.save(regisOfCurrentWinner);
            userRepository.save(curW);
          });
      Bid bid = new Bid(user, auction);
      bid.setBidPrice(bidRequest.getBidPrice());
      auction.setCurrentPrice(bidRequest.getBidPrice());
      user.freeze(auction.getWarranty());
      //      auction.setWarranty(nowWarranty);
      auction.setCurrentWinner(user.getId());
      auctionRegister.setWarranty(preWarranty);
      auctionRegisterRepository
          .findByUser_IdAndAuction_Id(user.getId(), auction.getId())
          .get()
          .setDepositing(true);
      //      assetRepository.save(asset);
      bidRepository.save(bid);
      userRepository.save(user);
      auctionRepository.save(auction);
      auctionRegisterRepository.save(auctionRegister);
      HashMap<String, String> val = new HashMap<String, String>();
      val.put("id", bid.getId() + "");
      val.put("price", bid.getBidPrice() + "");
      val.put("auction", auction.getId() + "");
      val.put("user", user.getId() + "");
      val.put("username", user.getName());
      val.put("created", bid.getCreated() + "");
      val.put("avatar", user.getAvatar());
      mqttService.sendToMqtt(val, "auction/bid");
    }
  }

  public void bidReverse(
      User user, Asset asset, Optional<Auction> auctionOptional, @RequestBody BidRequest bidRequest)
      throws JsonProcessingException {
    Bid bid = new Bid();
    bid.setUser(user);
    bid.setAuction(auctionOptional.get());
    bid.setBidPrice(bid.getBidPrice().add(new BigDecimal(200)));
    user.setBidTurn(user.getBidTurn() - 1);
    bidRepository.save(bid);
    auctionOptional.get().setCurrentPrice(bidRequest.getBidPrice());
    assetRepository.save(asset);
    HashMap<String, String> val = new HashMap<String, String>();
    val.put("id", bid.getId() + "");
    val.put("price", bid.getBidPrice() + "");
    val.put("auction", auctionOptional.get().getId() + "");
    val.put("user", user.getId() + "");
    val.put("username", user.getName());
    mqttService.sendToMqtt(val, "auction/bid");
  }
}
