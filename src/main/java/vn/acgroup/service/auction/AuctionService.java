package vn.acgroup.service.auction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.acgroup.config.LightwalletConfig;
import vn.acgroup.controllers.api.WithdrawRequest;
import vn.acgroup.entities.Address;
import vn.acgroup.entities.Asset;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.AuctionRegister;
import vn.acgroup.entities.Bid;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.AddressRepository;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.BidRepository;
import vn.acgroup.repositories.LikedAuctionRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.googleSheet.GoogleService;
import vn.acgroup.service.mail.MailService;
import vn.acgroup.service.telegramBot.TeleBotService;
import vn.acgroup.service.wallet.WalletService;

@Service
public class AuctionService {
  @Autowired UserRepository userRepository;

  @Autowired AddressRepository addressRepository;

  @Autowired AuctionRegisterRepository auctionRegisterRepository;

  @Autowired WalletService walletService;

  @Autowired MailService mailService;

  @Autowired AuctionRepository auctionRepository;

  @Autowired BidRepository bidRepository;

  @Autowired GoogleService googleService;

  @Autowired TeleBotService teleBotService;

  @Autowired LikedAuctionRepository likedAuctionRepository;

  Logger log = Logger.getLogger(AuctionService.class.getName());

  public Optional<User> endAuction(Auction auction) throws CustomException {

    Optional<Iterable<AuctionRegister>> auctionRegisterOptional =
        auctionRegisterRepository.findByAuctionAndIsDeleted(auction, false);
    Optional<User> winner = userRepository.findById(auction.getCurrentWinner());
    AuctionRegister autionRegisterWinner = new AuctionRegister();
    if (auctionRegisterOptional.isPresent()) {
      for (AuctionRegister auctionRegister : auctionRegisterOptional.get()) {
        User user = auctionRegister.getUser();
        if (!winner.isPresent() || winner.get().getId() != user.getId()) {
          auctionRegister.setIsDeleted(true);
          userRepository.save(user);
          auctionRegisterRepository.save(auctionRegister);
        }
        if (auctionRegister.getUser().getId() == winner.get().getId()
            && auctionRegister.getAuction().getId() == auction.getId()) {
          autionRegisterWinner = auctionRegister;
        }
      }
    }
    Address address = new Address();
    if (winner.isPresent()) {
      auction.setWinner(winner.get().getId());
      mailService.winMail(winner.get(), auction);
      log.info("sended mail");
      Optional<Address> addressOp =
          addressRepository.findByUserAndIsDefault(winner.get().getId(), true);
      if (!addressOp.isPresent()) {
        address = new Address();
      } else {
        address = addressOp.get();
      }
      auction.setAddressId(address.getId());
    }
    auction.setWinPrice(auction.getCurrentPrice());
    auction.setEndAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    auction.setStatus("Ended");

    log.info("aution status to saved: " + auction.getId() + " status " + auction.getStatus());
    auctionRepository.save(auction);
    likedAuctionRepository.deleteLikeAuction(auction.getId());

    int bids = bidRepository.countByAuction(auction);
    googleService.exportAuctionToGoogleSheet(
        auction, bids, winner.get(), autionRegisterWinner, address);
    teleBotService.notifiWinAuction(auction, winner.get());

    return winner;
  }

  public String pay(long auction_id, long user_id) throws CustomException {
    User user = userRepository.findById(user_id).get();
    Auction auction = auctionRepository.findById(auction_id).get();
    if (auction.getWinner() != user.getId()) return ("Bạn không phải người thắng đấu giá này");
    Asset asset = auction.getAssest();
    long time =
        ChronoUnit.SECONDS.between(
            auction.getEndAt(), LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    if (time > 5 * 24 * 60 * 60) {
      return ("Hết hạn thanh toán");
    }
    if (!auction.getStatus().equals("Ended"))
      return ("Trạng thái của đấu giá : " + auction.getStatus());
    Optional<AuctionRegister> auctionRegisterO =
        auctionRegisterRepository.findByUserAndAuctionAndIsDeleted(user, auction, false);
    if (!auctionRegisterO.isPresent() || auctionRegisterO.get().getWarranty() == null)
      return ("Không tìm thấy lịch sử đăng kí đấu giá hoặc cọc đấu giá");
    AuctionRegister auctionRegister = auctionRegisterO.get();
    if (user.getVNDTBalance().add(auctionRegister.getWarranty()).compareTo(auction.getWinPrice())
        < 0) return ("Không đủ tiền thanh toán");
    WithdrawRequest withdrawRequest =
        new WithdrawRequest(
            LightwalletConfig.productAddress,
            auction.getWinPrice(),
            "Khách hàng " + user.getId() + " thanh toán đấu giá " + auction_id);
    try {
      walletService.sendVNDT(user, withdrawRequest);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    auctionRegister.setDepositing(false);
    auctionRegister.cancel();
    auctionRegisterRepository.save(auctionRegister);

    user.unfreeze(auction.getWarranty());
    userRepository.save(user);

    auction.setStatus("Paid");
    auctionRepository.save(auction);
    return ("OK");
  }

  public String sellOff(long auction_id, long user_id) throws CustomException {
    User user = userRepository.findById(user_id).get();
    Auction auction = auctionRepository.findById(auction_id).get();
    if (auction.getWinner() != user.getId()) return ("Bạn không phải người thắng đấu giá này");
    long time =
        ChronoUnit.SECONDS.between(
            auction.getEndAt(), LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    if (time > 5 * 24 * 60 * 60) {
      return ("Hết hạn thanh toán");
    }
    if (!auction.getStatus().equals("Ended"))
      return ("Trạng thái của đấu giá : " + auction.getStatus());
    Optional<AuctionRegister> auctionRegisterO =
        auctionRegisterRepository.findByUserAndAuctionAndIsDeleted(user, auction, false);
    if (!auctionRegisterO.isPresent() || auctionRegisterO.get().getWarranty() == null)
      return ("Không tìm thấy lịch sử đăng kí đấu giá hoặc cọc đấu giá");

    int count = auctionRepository.countByWinnerAndStatus(user.getId(), "Paid");
    if (count < 3) {
      return ("Trước đó bạn phải mua tối thiểu 3 sản phẩm mới có thể thực hiện thao tác thanh lý đấu giá trúng");
    }

    AuctionRegister auctionRegister = auctionRegisterO.get();

    BigDecimal sellOffAmount =
        auction.getBuyPrice().multiply(new BigDecimal(auction.getSellOffPercent()));
    BigDecimal refundAmount = sellOffAmount.subtract(auction.getWinPrice());

    WithdrawRequest withdrawRequest =
        new WithdrawRequest(
            user.getWalletAddress(),
            refundAmount,
            "Thanh lý đấu giá " + auction_id + " cho khách hàng " + user.getId());

    User admin = userRepository.findById(LightwalletConfig.adminProduct).get();
    try {
      walletService.sendBonus(admin, withdrawRequest);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    auction.setStatus("SellOff");
    auctionRegister.cancel();
    auctionRegisterRepository.save(auctionRegister);
    // user.unfreeze(auctionRegister.getWarranty());
    // userRepository.save(user);
    return ("OK");
  }

  public ResponseEntity refuse(long auction_id, long user_id) throws CustomException {
    User user = userRepository.findById(user_id).get();
    Auction auction =
        auctionRepository
            .findById(auction_id)
            .orElseThrow(
                () ->
                    new CustomException(
                        "Auction with id " + auction_id + " not found",
                        HttpStatus.NOT_FOUND.value()));
    if (auction.getWinner() != user.getId())
      return new ResponseEntity<>(user.getName() + " aren't winner", HttpStatus.FORBIDDEN);
    if (!auction.getStatus().equals("Ended"))
      return new ResponseEntity<>(
          "Auction status is: " + auction.getStatus(), HttpStatus.FORBIDDEN);
    AuctionRegister auctionRegister =
        auctionRegisterRepository
            .findByUserAndAuctionAndIsDeleted(user, auction, false)
            .orElseThrow(() -> new CustomException("Error", HttpStatus.FORBIDDEN.value()));
    auctionRegister.cancel();
    auctionRegisterRepository.save(auctionRegister);
    Bid bid =
        bidRepository.findTop1ByAuction_idAndStatusOrderByCreatedDesc(auction_id, "true").get();
    bid.setStatus("false");
    bidRepository.save(bid);
    if (!auctionRegister.getWarranty().equals(0)) {
      WithdrawRequest withdrawRequest =
          new WithdrawRequest(
              LightwalletConfig.productAddress,
              auctionRegister.getWarranty(),
              "Trừ phí từ chối kết quả đấu giá " + auction_id);
      walletService.sendVNDT(user, withdrawRequest);
      if (user.getBonusBalance().compareTo(BigDecimal.ZERO) > 0)
        user.setBonusBalance(user.getBonusBalance().subtract(auctionRegister.getWarranty()));
      if (user.getBonusBalance().compareTo(BigDecimal.ZERO) < 0)
        user.setBonusBalance(BigDecimal.ZERO);
    }
    auction.setStatus("Refused");
    auctionRepository.save(auction);
    userRepository.save(user);
    mailService.refuseMail(user, auction);
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  public ResponseEntity checkAuctionExpires() throws Exception {
    LocalDateTime fiveDaysBefor = LocalDateTime.now().plusDays(-5);
    auctionRepository
        .findByStatusAndEndAtLessThan("Ended", fiveDaysBefor)
        .forEach(
            auction -> {
              log.info(
                  "process auction expires : "
                      + auction.getId()
                      + " winner :"
                      + auction.getWinner());
              if (!userRepository.findById(auction.getWinner()).isPresent()) {
                auction.setStatus("Expires");
                auctionRepository.save(auction);
                return;
              }
              User winner = userRepository.findById(auction.getWinner()).get();

              Optional<AuctionRegister> auctionRegisterOp =
                  auctionRegisterRepository.findByUserAndAuctionAndIsDeleted(
                      winner, auction, false);
              if (!auctionRegisterOp.isPresent()) {
                log.severe("Auction Register not found");
              } else {
                AuctionRegister auctionRegister = auctionRegisterOp.get();
                log.info("auctionRegister id: " + auctionRegister.getId());
                auctionRegister.cancel();
                auctionRegisterRepository.save(auctionRegister);

                if (auctionRegister.getWarranty().floatValue() > 0) {
                  WithdrawRequest withdrawRequest =
                      new WithdrawRequest(
                          LightwalletConfig.productAddress,
                          auctionRegister.getWarranty(),
                          "Trừ tiền cọc khi hết hạn thanh toán kết quả đấu giá " + auction.getId());
                  walletService.sendVNDT(winner, withdrawRequest);
                  winner.unfreeze(auctionRegister.getWarranty());
                  userRepository.save(winner);
                }
                mailService.expiresMail(winner, auction, auctionRegister.getWarranty());
              }
              auction.setStatus("Expires");
              auctionRepository.save(auction);
            });
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  public List<Map<String, Object>> findAuctionByStatus(String status, long limit)
      throws CustomException {
    if (null == status || status.isEmpty()) status = "Active";
    else if (status.equals("Active") && status.equals("Upcoming") && status.equals("Upcoming"))
      status = "Active";

    return auctionRepository.findAuctionByStatus(status, limit);
  }
  
  
  public Map<String,String> payList(List<Long> auction_id, long user_id) throws CustomException {
	  	Map<String,String> result = new HashMap<String, String>();
	  	BigDecimal totalAmount = BigDecimal.ZERO;
	  	BigDecimal totalWarranty = BigDecimal.ZERO;
	    User user = userRepository.findById(user_id).get();	    
	    Iterable<Auction> auctions = auctionRepository.findByIdIn(auction_id);	    
	    Iterable<AuctionRegister> auctionRegisters = auctionRegisterRepository.findByUserIdAndIsDeletedAndAuctionIdIn(user_id,false,auction_id);	    
	    for (Auction e: auctions) {
	    	AuctionRegister auctionRegister = null;	
		    for (AuctionRegister auc : auctionRegisters) {
		   		if(auc.getAuction().getId() == e.getId()) {
	    			 auctionRegister = auc;
	    			 totalWarranty = totalWarranty.add(totalWarranty);
	    		}  
			}
	    	if(auctionRegister == null) result.put(String.valueOf(e.getId()), "Không tìm thấy lịch sử đăng kí đấu giá hoặc cọc đấu giá");
	    	if (Strings.isBlank(result.get(String.valueOf(e.getId()))) && e.getWinner() != user.getId()) result.put(String.valueOf(e.getId()), "Bạn không phải người thắng đấu giá này");
	    	long time = ChronoUnit.SECONDS.between(e.getEndAt(), LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
	    	if (Strings.isBlank(result.get(String.valueOf(e.getId())))   &&  (time > 5 * 24 * 60 * 60))
	    		result.put(String.valueOf(e.getId()), "Hết hạn thanh toán");  	
	    	totalAmount = totalAmount.add(e.getWinPrice());		
		}	    	    	    
	    if (user.getVNDTBalance().add(totalWarranty).compareTo(totalAmount) < 0)result.put("balance", "Không đủ tiền thanh toán");	    	    
	    if (result.size() == 0) {
	    	 user.unfreeze(totalWarranty);
			 userRepository.save(user);
	    	 auctions.forEach(auction -> {
	    		 WithdrawRequest withdrawRequest = new WithdrawRequest(
				            LightwalletConfig.productAddress,
				            auction.getWinPrice(),
				            "Khách hàng " + user.getId() + " thanh toán đấu giá " + auction_id);
				    try {
				      walletService.sendVNDT(user, withdrawRequest);
				    } catch (Exception e) {
				      System.out.println(e.getMessage());
				    }
	    		 
	    	});
	    	auctionRegisterRepository.updateAuctionRegisterPayList(user_id,auction_id);
	    	auctionRepository.updateAuctionPayList("Paid", auction_id);
	 	       
		    result.put("status", "OK");
		}else result.put("status", "ERROR");
	    return result;
	  }
  
  

}
