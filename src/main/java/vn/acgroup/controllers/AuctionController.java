package vn.acgroup.controllers;

import java.util.concurrent.ExecutionException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.config.LightwalletConfig;
import vn.acgroup.controllers.api.AddAuctionRequest;
import vn.acgroup.controllers.api.FindNewAuctionResponse;
import vn.acgroup.controllers.api.WithdrawRequest;
import vn.acgroup.entities.Asset;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.AuctionRegister;
import vn.acgroup.entities.Bid;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.BidRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.auction.AuctionService;
import vn.acgroup.service.mail.MailService;
import vn.acgroup.service.wallet.WalletService;
import vn.acgroup.repositories.AddressRepository;
import vn.acgroup.repositories.AssetRepository;
import vn.acgroup.repositories.AuctionRegisterRepository;

@Controller
@CrossOrigin(origins = "*")
public class AuctionController {

  @Autowired AuctionRepository auctionRepository;

  @Autowired AssetRepository assetRepository;

  @Autowired UserRepository userRepository;

  @Autowired BidRepository bidRepository;
  @Autowired AuctionRegisterRepository auctionRegisterRepository;
  @Autowired AddressRepository addressRepository;
  @Autowired RestTemplate lightWalletRestTemplate;

  @Autowired
  @Qualifier("bonusWalletTemplate")
  RestTemplate bonusWalletTemplate;

  @Autowired MailService mailService;
  @Autowired WalletService walletService;
  @Autowired AuctionService auctionService;

  @GetMapping(value = "/auction")
  @ResponseBody
  public Iterable<Auction> getAll() throws InterruptedException, ExecutionException {
    return auctionRepository.findAll();
  }

  @GetMapping(value = "/auction/{id}")
  @ResponseBody
  public Optional<Auction> getAuctionById(@PathVariable long id)
      throws InterruptedException, ExecutionException {
    return auctionRepository.findById(id);
  }

  @PostMapping(value = "/auction/")
  @ResponseBody
  @ApiOperation(
      value = "auctionAdd",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity auctionAdd(@RequestBody AddAuctionRequest addAuctionRequest)
      throws InterruptedException, ExecutionException, CustomException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user =
        userRepository
            .findByEmailAndIsActive((String) principal, true)
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND.value()));
    Asset asset =
        assetRepository
            .findById(addAuctionRequest.getAssest())
            .orElseThrow(
                () -> new CustomException("Asset not found", HttpStatus.NOT_FOUND.value()));

    BigDecimal bidPrice = addAuctionRequest.getBidPrice();

    LocalDateTime start = addAuctionRequest.getStartAt();
    LocalDateTime end = addAuctionRequest.getEndAt();
    LocalDateTime atendance = addAuctionRequest.getAttendanceDeadline();
    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    BigDecimal warranty = addAuctionRequest.getWarranty();
    if (!(atendance.isAfter(now) && start.isAfter(atendance) && end.isAfter(start)))
      return new ResponseEntity<>("Thời gian đấu giá không hợp lệ", HttpStatus.FORBIDDEN);
    if (bidPrice.compareTo(BigDecimal.ZERO) <= 0)
      return new ResponseEntity<>("Số tiền đấu giá không hợp lệ", HttpStatus.FORBIDDEN);
    if (warranty.compareTo(asset.getInitPrice().multiply(BigDecimal.valueOf(0))) < 0
        || warranty.compareTo(asset.getInitPrice()) > 0)
      return new ResponseEntity<>("Số tiền đặt cọc không hợp lệ", HttpStatus.FORBIDDEN);
    if (!userRepository.findById(addAuctionRequest.getSeller()).isPresent())
      return new ResponseEntity<>("Không tìm thấy người dùng", HttpStatus.NOT_FOUND);
    if (addAuctionRequest.getAttendingUser() < 0)
      return new ResponseEntity<>("Số người tham gia đấu giá không hợp lệ", HttpStatus.NOT_FOUND);
    Auction auction = new Auction(user, addAuctionRequest);
    auction.setAssest(asset);
    auction.setCategory(asset.getCategory());
    auction.setCurrentPrice(BigDecimal.ZERO);
    //    asset.setCurrentPrice(auction.getBidPrice());
    auctionRepository.save(auction);
    //    assetRepository.save(asset);
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  @PostMapping(value = "/auction/update/{id}")
  @ApiOperation(
      value = "update/id",
      authorizations = {@Authorization(value = "JWT")})
  @ResponseBody
  public ResponseEntity update(@PathVariable long id, String status, String note)
      throws InterruptedException, ExecutionException {
    try {
      Auction auction = auctionRepository.findById(id).get();
      auction.setStatus(status);
      auction.setNote(note);
      auctionRepository.save(auction);
      if (auction.getStatus().equals("Active") || auction.getStatus().equals("Upcoming")) {
        User user = userRepository.findUserByAuctions_Id(id);
        Asset asset = assetRepository.findAssetByAuctions_Id(id);
        RediSearch.getData(auction, user, asset, userRepository, RediSearch.client);
      }
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping(value = "/auction/showInBaner/{id}")
  @ApiOperation(
      value = "showInBaner/id",
      authorizations = {@Authorization(value = "JWT")})
  @ResponseBody
  public ResponseEntity showInBaner(@PathVariable long id, char showInBaner)
      throws InterruptedException, ExecutionException {
    try {
      Auction auction = auctionRepository.findById(id).get();
      auction.setShowInBaner(showInBaner);
      auctionRepository.save(auction);
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "/auction/category/{name}")
  @ApiOperation(value = "category")
  @ResponseBody
  public Iterable<Auction> findAuctionByCategory(@PathVariable String name)
      throws InterruptedException, ExecutionException {
    return auctionRepository.findByCategory(name);
  }

  @GetMapping(value = "/auction/auction_status/{string}") // search dài
  @ApiOperation(value = "duyệt new auction")
  @ResponseBody
  public List<FindNewAuctionResponse> findAuctionByStatus(@PathVariable String string)
      throws InterruptedException, ExecutionException {
    List<FindNewAuctionResponse> list = new ArrayList<>();
    Iterable<Auction> auctionIterable = auctionRepository.findByStatus(string);
    auctionIterable.forEach(
        auction -> {
          FindNewAuctionResponse response = new FindNewAuctionResponse();
          response.setAuction(auction);
          response.setAsset(assetRepository.findAssetByAuctions_Id(auction.getId()));
          response.getAsset().setAuctions(null);
          response.setUser(userRepository.findUserByAuctions_Id(auction.getId()));
          response.getUser().setAssets(null);
          response.getUser().setAuctions(null);
          list.add(response);
        });
    return list;
  }

  @GetMapping(value = "/auction/test-google")
  @ApiOperation(value = "test add aution to google sheet")
  @ResponseBody
  public String testGoogle() {

    List<User> users = userRepository.findByIsActive(false);
    List<User> savedUser = new ArrayList<User>();
    for (User user : users) {
      System.out.println(user.getEmail());
      user.setIsActive(true);
      savedUser.add(user);
    }
    userRepository.saveAll(savedUser);

    return "OK";
  }

  @GetMapping(value = "/auction/status/{string}") // search ngắn
  @ApiOperation(value = "tìm để in ra auction")
  @ResponseBody
  public Iterable<Auction> findByStatus(@PathVariable String string)
      throws InterruptedException, ExecutionException {
    return auctionRepository.findByStatus(string);
  }

  @GetMapping(value = "/auction/winner/{id}")
  @ApiOperation(value = "find winner of auction")
  @ResponseBody
  public ResponseEntity<?> findWinnerOfAuction(@PathVariable Long id)
      throws InterruptedException, ExecutionException, CustomException {
    Auction auction = auctionRepository.findById(id).get();

    Optional<User> winner = userRepository.findById(auction.getCurrentWinner());
    winner.ifPresent(
        (winU) -> {
          List<Asset> assets = new ArrayList<>();
          assets.add(auction.getAssest());
          winU.setAssets(assets);
        });
    return new ResponseEntity<>(winner, HttpStatus.OK);
  }

  @GetMapping(value = "/auction/buy/{id}") // id Of Auction
  @ResponseBody
  @ApiOperation(
      value = "buy",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity buy(@PathVariable long id)
      throws InterruptedException, ExecutionException, CustomException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByEmailAndIsActive((String) principal, true).get();
    Auction auction = auctionRepository.findById(id).get();
    if (!auction.getStatus().equals("Upcoming"))
      return new ResponseEntity<>("Auction is " + auction.getStatus(), HttpStatus.FORBIDDEN);
    if (user.getVNDTBalance().compareTo(auction.getWarranty()) < 0)
      return new ResponseEntity<>(
          "Không đủ tiền đặt cọc, số tiền đặt cọc là " + auction.getWarranty(),
          HttpStatus.FORBIDDEN);
    Optional<AuctionRegister> auctionRegister =
        auctionRegisterRepository.findByUserAndAuction(user, auction);
    auctionRegister
        .filter(
            (auc) -> {
              return auc.getIsDeleted().equals(true)
                  && user.getVNDTBalance().compareTo(auction.getWarranty()) >= 0;
            })
        .ifPresent(
            (auc) -> {
              auc.setIsDeleted(false);
              auc.setWarranty(auction.getWarranty());
              // user.freeze(auction.getWarranty());
              auctionRegisterRepository.save(auc);
            });
    auctionRegister
        .filter(
            (auc) -> {
              return auc.getIsDeleted().equals(false)
                  && user.getVNDTBalance().compareTo(auction.getWarranty()) >= 0;
            })
        .ifPresent(
            (auc) -> {
              auc.setWarranty(auction.getWarranty());
              // user.freeze(auction.getWarranty());
              auctionRegisterRepository.save(auc);
            });

    auctionRegister.orElseGet(
        () -> {
          if (user.getVNDTBalance().compareTo(auction.getWarranty()) >= 0) {
            AuctionRegister newReg = new AuctionRegister(user, auction);
            newReg.setWarranty(auction.getWarranty());
            // user.freeze(auction.getWarranty());
            auctionRegisterRepository.save(newReg);
          }
          return null;
        });
    user.freeze(auction.getWarranty());
    Asset asset = auction.getAssest();
    // asset.setCurrentPrice(auction.getBuyPrice());
    auction.setWinPrice(auction.getBuyPrice());
    Bid bid = new Bid(user, auction);
    bid.setBidPrice(auction.getBuyPrice());
    bidRepository.save(bid);
    auction.setWinner(user.getId());
    auction.setEndAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    auction.setStatus("Ended");
    assetRepository.save(asset);
    auctionRepository.save(auction);
    userRepository.save(user);

    User admin = userRepository.findById(LightwalletConfig.adminId).get();
    BigDecimal quantity = auction.getRegistrationFee();
    auctionRegisterRepository
        .findByAuction_IdAndIsDeleted(auction.getId(), false)
        .ifPresent(
            res -> {
              res.forEach(
                  reg -> {
                    WithdrawRequest withdrawRequest =
                        new WithdrawRequest(
                            reg.getUser().getBonusAddress(),
                            quantity,
                            "Hoàn phí đăng ký đấu giá " + auction.getId());
                    walletService.sendBonus(admin, withdrawRequest);
                  });
            });
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  // @GetMapping(value = "/auction/refund-to-email")
  // @ResponseBody
  // @ApiOperation(value = "rfund to special user", authorizations = {
  // @Authorization(value = "JWT")
  // })
  // public String refundToUser(@RequestParam("email") String email,
  // @RequestParam("quantity") long
  // quantity) {
  //
  //// Auction aution = auctionRepository.findById(id).get();
  // User user = userRepository.findByEmailAddress(email);
  //
  // User admin = userRepository.findById(LightwalletConfig.adminId).get();
  //
  // System.out.println("send refund to " + user.getEmail() + " amount " +
  // quantity);
  // WithdrawRequest withdrawRequest = new WithdrawRequest(user.getBonusAddress(),
  // new BigDecimal(
  // quantity),
  // "Hoàn phí đăng ký đấu giá 119012");
  // walletService.sendBonus(admin, withdrawRequest);
  // return "OK";
  // }

  // @GetMapping(value = "/auction/refund-fee")
  // @ResponseBody
  // @ApiOperation(
  // value = "tra coc",
  // authorizations = {@Authorization(value = "JWT")})
  // public String refundFee(@RequestParam ("auction") long id ) {
  //
  // Auction aution = auctionRepository.findById(id).get();
  //
  // User admin = userRepository.findById(LightwalletConfig.adminId).get();
  // BigDecimal quantity = aution.getRegistrationFee();
  // auctionRegisterRepository
  // .findByAuction_IdAndIsDeleted(aution.getId(), true)
  // .ifPresent(
  // res -> {
  // res.forEach(
  // reg -> {
  // try {
  // System.out.println("send refund to "+ reg.getUser().getEmail()+" amount "
  // + quantity);
  // WithdrawRequest withdrawRequest =
  // new WithdrawRequest(
  // reg.getUser().getBonusAddress(),
  // quantity,
  // "Hoàn phí đăng ký đấu giá " + aution.getId());
  // walletService.sendBonus(admin, withdrawRequest);
  // Thread.sleep(1000);
  // } catch (InterruptedException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // });
  // });
  //
  // return "OK";
  // }

  //    @GetMapping(value = "/auction/refund-fee")
  //    @ResponseBody
  //    @ApiOperation(
  //        value = "tra coc",
  //        authorizations = {@Authorization(value = "JWT")})
  //    public String refundFee(@RequestParam ("auction") long id ) {
  //
  //  	  Auction aution = auctionRepository.findById(id).get();
  //
  //  	  User admin = userRepository.findById(LightwalletConfig.adminId).get();
  //  	  BigDecimal quantity = aution.getRegistrationFee();
  //        auctionRegisterRepository
  //            .findByAuction_IdAndIsDeleted(aution.getId(), true)
  //            .ifPresent(
  //                res -> {
  //                  res.forEach(
  //                      reg -> {
  //                      try {
  //                      	System.out.println("send refund to "+ reg.getUser().getEmail()+" amount "
  // + quantity);
  //                        WithdrawRequest withdrawRequest =
  //                            new WithdrawRequest(
  //                                reg.getUser().getBonusAddress(),
  //                                quantity,
  //                                "Hoàn phí đăng ký đấu giá " + aution.getId());
  //                        walletService.sendBonus(admin, withdrawRequest);
  //                      	Thread.sleep(1000);
  //  					} catch (InterruptedException e) {
  //  						// TODO Auto-generated catch block
  //  						e.printStackTrace();
  //  					}
  //                     });
  //                });
  //
  //  	  return "OK";
  //    }

  @GetMapping(value = "/auction/end/{id}") // id Of Auction
  @ResponseBody
  @ApiOperation(
      value = "tra coc",
      authorizations = {@Authorization(value = "JWT")})
  public Optional<User> end(@PathVariable long id) throws InterruptedException, ExecutionException {
    final Logger log = LoggerFactory.getLogger(AuctionService.class);
    Auction auction = auctionRepository.findById(id).get();
    Optional<Iterable<AuctionRegister>> auctionRegisterOptional =
        auctionRegisterRepository.findByAuctionAndIsDeleted(auction, false);
    Optional<User> winner = userRepository.findById(auction.getCurrentWinner());
    if (auctionRegisterOptional.isPresent()) {
      for (AuctionRegister auctionRegister : auctionRegisterOptional.get()) {
        User user = auctionRegister.getUser();
        if (!winner.isPresent() || winner.get().getId() != user.getId()) {
          auctionRegister.setIsDeleted(true);
          userRepository.save(user);
          auctionRegisterRepository.save(auctionRegister);
        }
      }
      if (winner.isPresent()) {
        auction.setWinner(winner.get().getId());
        mailService.winMail(winner.get(), auction);
        log.info("send mail");
      }
    }
    //    auction.setWinPrice(auction.getAssest().getCurrentPrice());
    auction.setWinPrice(auction.getCurrentPrice());
    auction.setEndAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    auction.setStatus("Ended");
    auctionRepository.save(auction);
    return winner;
  }

  @GetMapping(value = "/auction/pay/dl/{user_id}/{auction_id}") // id1- User, id2 - Auction
  @ResponseBody
  @ApiOperation(
      value = "thanh toán qua đại lí",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity pay(@PathVariable long user_id, @PathVariable long auction_id)
      throws InterruptedException, ExecutionException, CustomException {
    Auction auction =
        auctionRepository
            .findById(auction_id)
            .orElseThrow(
                () ->
                    new CustomException(
                        "Auction with id " + auction_id + " not found",
                        HttpStatus.NOT_FOUND.value()));
    User user =
        userRepository
            .findById(user_id)
            .orElseThrow(
                () ->
                    new CustomException(
                        "User with id " + user_id + " not found", HttpStatus.NOT_FOUND.value()));
    if (auction.getWinner() != user_id)
      return new ResponseEntity<>(user_id + " aren't winner", HttpStatus.FORBIDDEN);
    Asset asset = auction.getAssest();
    long time =
        ChronoUnit.SECONDS.between(
            auction.getEndAt(), LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    if (time > 5 * 24 * 60 * 60) {
      return new ResponseEntity<>("Expired payment", HttpStatus.FORBIDDEN);
    }

    if (!auction.getStatus().equals("Ended"))
      return new ResponseEntity<>(
          "Auction status is: " + auction.getStatus(), HttpStatus.FORBIDDEN);
    user.unfreeze(auction.getWarranty());

    auctionRegisterRepository
        .findByUserAndAuctionAndIsDeleted(user, auction, false)
        .ifPresent(
            (auctionRegister) -> {
              auctionRegister.setIsDeleted(true);
              auctionRegisterRepository.save(auctionRegister);
            });
    auction.setStatus("Paid");

    assetRepository.save(asset);
    auctionRepository.save(auction);
    userRepository.save(user);
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

  // @GetMapping(value = "/auction/refuse/{id}")
  // @ResponseBody
  // @ApiOperation(
  // value = "từ chối đấu giá",
  // authorizations = {@Authorization(value = "JWT")})
  // public ResponseEntity refuse(@PathVariable long id)
  // throws InterruptedException, ExecutionException, CustomException {
  // Object principal =
  // SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  // User user =
  // userRepository
  // .findByEmailAndIsActive((String) principal, true)
  // .orElseThrow(() -> new CustomException("User not found",
  // HttpStatus.NOT_FOUND.value()));
  // Auction auction =
  // auctionRepository
  // .findById(id)
  // .orElseThrow(
  // () ->
  // new CustomException(
  // "Auction with id " + id + " not found",
  // HttpStatus.NOT_FOUND.value()));
  // if (auction.getWinner() != user.getId())
  // return new ResponseEntity<>(user.getName() + " aren't winner",
  // HttpStatus.FORBIDDEN);
  // if (!auction.getStatus().equals("Ended"))
  // return new ResponseEntity<>(
  // "Auction status is: " + auction.getStatus(), HttpStatus.FORBIDDEN);
  // user.setVNDTFreeze(user.getVNDTFreeze().subtract(auction.getWarranty()));
  // AuctionRegister auctionRegister =
  // auctionRegisterRepository
  // .findByUserAndAuctionAndIsDeleted(user, auction, false)
  // .orElseThrow(() -> new CustomException("Error",
  // HttpStatus.FORBIDDEN.value()));
  // auctionRegister.setIsDeleted(true);
  // auctionRegisterRepository.save(auctionRegister);
  // Bid bid = bidRepository.findTop1ByAuction_idAndStatusOrderByCreatedDesc(id,
  // "true").get();
  // bid.setStatus("false");
  // bidRepository.save(bid);
  // WithdrawRequest withdrawRequest =
  // new WithdrawRequest(
  // LightwalletConfig.address, auction.getWarranty(), "Trừ phí từ chối kết quả
  // đấu
  // giá");
  // walletService.sendVNDT(user, withdrawRequest);
  // bidRepository
  // .findTop1ByAuction_idAndStatusOrderByCreatedDesc(id, "true")
  // .ifPresent(
  // (bid2) -> {
  // if
  // (bid2.getBidPrice().add(auction.getWarranty()).compareTo(bid.getBidPrice()) <
  // 0 ) {
  // auction.setStatus("Time out");
  // auction.setWinner(Long.valueOf(0));
  // auctionRepository.save(auction);
  // // return new ResponseEntity<>("Không có người thắng đấu giá.",
  // HttpStatus.OK);
  // } else {
  // User winnner = bid2.getUser();
  // auction.setWinner(winnner.getId());
  // auctionRepository.save(auction);
  // // return new ResponseEntity<>("OK", HttpStatus.FORBIDDEN);
  // }
  // });
  // return new ResponseEntity<>(auction.getWinner(), HttpStatus.OK);
  // return new ResponseEntity<>("Chưa làm api", HttpStatus.OK);
  // }

  @GetMapping(value = "/auction/pay/{id}") // id Of Auction
  @ResponseBody
  @ApiOperation(
      value = "thanh toán ",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity pay(@PathVariable long id) throws Exception {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user =
        userRepository
            .findByEmailAndIsActive((String) principal, true)
            .orElseThrow(() -> new Exception("User not found"));
    String result = auctionService.pay(id, user.getId());
    if (result.equals("OK")) return new ResponseEntity<>(result, HttpStatus.OK);
    else return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
  }

  @GetMapping(value = "/auction/sell-off/{id}") // id Of Auction
  @ResponseBody
  @ApiOperation(
      value = "ban thanh ly lai cho cty dau gia",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity sellOff(@PathVariable long id) throws Exception {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user =
        userRepository
            .findByEmailAndIsActive((String) principal, true)
            .orElseThrow(() -> new Exception("User not found"));
    String result = auctionService.sellOff(id, user.getId());
    if (result.equals("OK")) return new ResponseEntity<>(result, HttpStatus.OK);
    else return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
  }

  @GetMapping(value = "/auction/refuse/{id}")
  @ResponseBody
  @ApiOperation(
      value = "từ chối thanh toán",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity refuse(@PathVariable long id) throws Exception {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user =
        userRepository
            .findByEmailAndIsActive((String) principal, true)
            .orElseThrow(() -> new Exception("User not found"));
    return auctionService.refuse(id, user.getId());
  }

  @GetMapping(value = "/auction/recreate/{id}")
  @ResponseBody
  @ApiOperation(
      value = "tạo lại auction từ auction đã có",
      authorizations = {@Authorization(value = "JWT")})
  public HashMap<String, Object> recreate(@PathVariable long id) throws Exception {
    HashMap<String, Object> reAuction = new HashMap<String, Object>();
    auctionRepository
        .findById(id)
        .ifPresent(
            auction -> {
              reAuction.put("area", auction.getArea());
              reAuction.put("assest", auction.getAssest().getId());
              reAuction.put("attendanceDeadline", auction.getAttendanceDeadline());
              reAuction.put("attendingUser", auction.getAttendingUser());
              reAuction.put("bidPrice", auction.getBidPrice());
              reAuction.put("buyPrice", auction.getBuyPrice());
              reAuction.put("endAt", auction.getEndAt());
              reAuction.put("note", auction.getNote());
              reAuction.put("percent", auction.getPercent());
              reAuction.put("registrationFee", auction.getRegistrationFee());
              reAuction.put("regulation", auction.getRegulation());
              reAuction.put("seller", auction.getSeller());
              reAuction.put("startAt", auction.getStartAt());
              reAuction.put("stepPrice", auction.getStepPrice());
              reAuction.put("type", auction.getType());
              reAuction.put("warranty", auction.getWarranty());
            });
    return reAuction;
  }
  
  
  @PostMapping(value = "/auction/pay-list")
  @ResponseBody
  @ApiOperation(
      value = "Pay list ",
      authorizations = {@Authorization(value = "JWT")})
  public ResponseEntity payList(@RequestBody List<Long> auctionIds) throws Exception {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository
            .findByEmailAndIsActive((String) principal, true)
            .orElseThrow(() -> new Exception("User not found"));  
    Map<String,String> map = auctionService.payList(auctionIds, user.getId());  
//    String result = auctionService.pay(id, user.getId());
    if (map.get("status").equals("OK")) return new ResponseEntity<>(map, HttpStatus.OK);
    else return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
  }
  
}
