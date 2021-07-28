package vn.acgroup.controllers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.redisearch.AggregationResult;
import io.redisearch.aggregation.AggregationBuilder;
import io.redisearch.client.Client;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

import vn.acgroup.entities.Auction;
import vn.acgroup.entities.Address;
import vn.acgroup.entities.AuctionRegister;
import vn.acgroup.entities.LikedAuction;
import vn.acgroup.entities.News;
import vn.acgroup.entities.NoticeRegister;
import vn.acgroup.entities.User;
import vn.acgroup.entities.News;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.AddressRepository;
import vn.acgroup.repositories.AssetRepository;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.BidRepository;
import vn.acgroup.repositories.CategoryRepository;
import vn.acgroup.repositories.FeedbackRepository;
import vn.acgroup.repositories.LikedAuctionRepository;
import vn.acgroup.repositories.NewsRepository;
import vn.acgroup.repositories.NoticeRegisterRepository;
import vn.acgroup.repositories.TransactionRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.giftcode.GiftcodeService;
import vn.acgroup.service.mail.MailService;

@Controller
@CrossOrigin(origins = "*")
public class HomeController {

  Logger log = Logger.getLogger(this.getClass().getName());

  static Client client =
      new Client(
          "auction",
          "redis-19747.c238.us-central1-2.gce.cloud.redislabs.com",
          19747,
          1800,
          1000,
          "iuvr2bYMJJD4nC1rFKAPkmeskhHLAp2J");
  @Autowired AuctionRepository auctionRepository;
  @Autowired CategoryRepository categoryRepository;
  @Autowired UserRepository userRepository;
  @Autowired AssetRepository assetRepository;
  @Autowired AddressRepository addressRepository;
  @Autowired AuctionRegisterRepository auctionRegisterRepository;
  @Autowired BidRepository bidRepository;
  @Autowired FeedbackRepository feedbackRepository;
  @Autowired TransactionRepository transactionRepository;
  @Autowired LikedAuctionRepository likedAuctionRepository;
  @Autowired NewsRepository newsRepository;
  @Autowired NoticeRegisterRepository noticeRegisterRepository;
  @Autowired GiftcodeService giftcodeService;
  @Autowired MailService mailService;

  @ControllerAdvice
  public class MvcAdvice {

    @ModelAttribute("liked_auction_top_3")
    public Iterable<LikedAuction> likeAuctionTop3()
        throws InterruptedException, ExecutionException {
      try {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
        return likedAuctionRepository.findTop3ByUserAndIsDelete(optional.get(), false).get();
      } catch (Exception e) {
        return null;
      }
    }

    @ModelAttribute("liked_auction_all")
    public Iterable<LikedAuction> likeAuctionAll() throws InterruptedException, ExecutionException {
      try {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
        return likedAuctionRepository.findByUserAndIsDelete(optional.get(), false).get();
      } catch (Exception e) {
        return null;
      }
    }

    @ModelAttribute("auctionAttendAll")
    public Iterable<AuctionRegister> attendAuctionAll()
        throws InterruptedException, ExecutionException {
      try {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
        return auctionRegisterRepository
            .findByUserAndIsDeletedAndAuction_status(optional.get(), false, "Active")
            .get();
      } catch (Exception e) {
        return null;
      }
    }

    @ModelAttribute("user")
    public User getUser() throws InterruptedException, ExecutionException {
      try {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmailAndIsActive((String) principal, true).get();
      } catch (Exception e) {
        return null;
      }
    }

    @ModelAttribute("system_time")
    public long systemTime() {
      return System.currentTimeMillis();
    }
  }

  @GetMapping(value = "/system-time")
  @ResponseBody
  @ApiOperation(
      value = "system-time",
      authorizations = {@Authorization(value = "JWT")})
  public long systemTime() throws InterruptedException, ExecutionException {
    return System.currentTimeMillis();
  }

  @GetMapping(value = "/")
  public String index(Model model) throws InterruptedException, ExecutionException {

    model.addAttribute("banner", true);
    model.addAttribute("category", "");
    model.addAttribute("categoriesTop6", categoryRepository.findTop6ByOrderByCreatedDesc());
    model.addAttribute(
        "auctionActiveTop6", auctionRepository.findTop6ByStatusOrderByCreatedAsc("Active"));
    model.addAttribute(
        "auctionActiveTop3", auctionRepository.findTop3ByStatusOrderByCreatedAsc("Active"));
    model.addAttribute(
        "auctionTop6Ended", auctionRepository.findTop6ByStatusOrderByEndAtDesc("Ended"));
    model.addAttribute(
        "auctionTop2Upcoming",
        auctionRepository.findTop2ByStatusAndShowInBanerOrOrStatusAndShowInBanerOrderByStartAtAsc(
            "Upcoming", '1', "Active", '1'));
    model.addAttribute(
        "auctionTop6Upcoming",
        auctionRepository.findTop6ByStatusAndShowInBanerOrderByStartAtAsc("Upcoming", '0'));
    model.addAttribute("newTop6", newsRepository.findTop6ByOrderByCreatedDesc());
    return "views/home/index";
  }

  @GetMapping(value = "/danh-sach-phien-dau-gia")
  public String danh_sach_phien_dau_gia(Model model, @RequestParam Map<String, String> reqParam)
      throws InterruptedException, ExecutionException {

    String sortBy = reqParam.get("sortBy");
    String category = reqParam.get("category");
    String order = reqParam.get("order");
    String status = reqParam.get("status");
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("category", category);
    model.addAttribute("order", order);
    model.addAttribute("status", status);
    model.addAttribute(
        "auctionActiveAndUpcoming", auctionRepository.findByStatusOrStatus("Active", "Upcoming"));
    model.addAttribute("categoryAll", categoryRepository.findAll());
    return "views/home/danh-sach-phien-dau-gia";
  }

  @GetMapping(value = "/dau-gia-sap-bat-dau")
  public String dau_gia_sap_bat_dau(Model model, @RequestParam Map<String, String> reqParam)
      throws InterruptedException, ExecutionException {

    String style = reqParam.get("style");
    String sortBy = reqParam.get("sortBy");
    String order = reqParam.get("order");
    model.addAttribute("style", style);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("order", order);
    model.addAttribute(
        "auctionSortByTime", auctionRepository.findByStatusOrderByStartAtAsc("Upcoming"));
    model.addAttribute(
        "auctionSortByPrice", auctionRepository.findByStatusOrderByBuyPriceAsc("Upcoming"));
    return "views/home/dau-gia-sap-bat-dau";
  }

  @GetMapping(value = "/dau-gia-da-ket-thuc")
  public String dau_gia_da_ket_thuc(Model model, @RequestParam Map<String, String> reqParam)
      throws InterruptedException, ExecutionException {
    String style = reqParam.get("style");
    String sortBy = reqParam.get("sortBy");
    model.addAttribute("style", style);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("auctionSortByPop", auctionRepository.findByStatus("Ended"));
    model.addAttribute(
        "auctionSortByTime", auctionRepository.findByStatusOrderByStartAtAsc("Ended"));
    model.addAttribute(
        "auctionSortByPrice", auctionRepository.findByStatusOrderByBuyPriceAsc("Ended"));
    return "views/home/dau-gia-da-ket-thuc";
  }

  @GetMapping(value = "/danh-muc-dau-gia")
  public String danh_muc_dau_gia(Model model) throws InterruptedException, ExecutionException {

    return "views/home/danh-muc-dau-gia";
  }

  @GetMapping(value = "/home")
  public String home() throws InterruptedException, ExecutionException {

    return "layout/home";
  }

  @GetMapping(value = "/danh-muc/{alias}")
  public String danh_muc_o_luoi(
      @PathVariable String alias, @RequestParam Map<String, String> reqParam, Model model)
      throws InterruptedException, ExecutionException {
    String style = reqParam.get("style");
    String sortBy = reqParam.get("sortBy");
    model.addAttribute("style", style);
    model.addAttribute("sortBy", sortBy);
    String category = categoryRepository.findByAlias(alias).getName();
    model.addAttribute(
        "auctionSortByTime", auctionRepository.findByCategoryOrderByStartAtDesc(category));
    model.addAttribute(
        "auctionSortByPrice", auctionRepository.findByCategoryOrderByBuyPriceAsc(category));
    model.addAttribute("category", category);
    return "views/home/danh-muc";
  }

  public static StringBuilder titleFormat(String title) {
    StringBuilder result = new StringBuilder(title.length());
    String words[] = title.split("\\ ");
    for (int i = 0; i < words.length; i++) {
      if (words[i].matches("[^0-9]+"))
        result
            .append(Character.toUpperCase(words[i].charAt(0)))
            .append(words[i].substring(1))
            .append(" ");
      else {
        result.append(words[i].toUpperCase()).append(" ");
      }
    }
    return result;
  }

  @GetMapping(value = "/tim-kiem")
  public String tim_kiem(@RequestParam Map<String, String> reqParam, Model model)
      throws InterruptedException, ExecutionException {
    String category = reqParam.get("category");
    String keyword = reqParam.get("keyword");
    String sortBy = reqParam.get("sortBy");
    String status = reqParam.get("status");
    String order = reqParam.get("order");
    AggregationBuilder r = null;
    ArrayList<Object> res = new ArrayList<Object>();
    if (keyword.length() != 0) {
      keyword =
          keyword
              .toLowerCase()
              .replaceAll(" +", " ")
              .trim()
              .replaceAll("\\b(\\p{L}+)\\b", "$1*")
              .replaceAll(" ", "|");
      if (status != null && (status.equals("active") || status.equals("upcoming")))
        keyword = keyword + " @AuctionStatus:" + status;
      if (!category.equals("all")) keyword = keyword + " @Category:(" + category + ")";
      r =
          new AggregationBuilder(keyword)
              .load("Id")
              .load("Images")
              .load("timeEnd")
              .load("timeStart")
              .load("AuctionStatus")
              .load("Title")
              .load("price")
              .load("bidPrice")
              .load("LiveStream");
      if (sortBy == "time") r = r.sortByDesc("@" + sortBy);
      if (sortBy == "price" && order == "asc") r = r.sortByAsc("@" + sortBy);
      if (sortBy == "price" && order == "desc") r = r.sortByDesc("@" + sortBy);
      AggregationResult result = client.aggregate(r);
      for (int i = 0; i < result.getResults().size(); i++) {
        JSONObject json = new JSONObject();
        try {
          json.put("Id", result.getRow(i).getString("Id"));
          json.put("Images", result.getRow(i).getString("Images"));
          json.put("Title", titleFormat(result.getRow(i).getString("Title")));
          json.put("CurrentPrice", result.getRow(i).getString("price"));
          json.put("BidPrice", result.getRow(i).getString("bidPrice"));
          json.put("EndAt", result.getRow(i).getString("timeEnd"));
          json.put("StartAt", result.getRow(i).getString("timeStart"));
          json.put("Status", result.getRow(i).getString("AuctionStatus"));
          json.put("LiveStream", result.getRow(i).getString("LiveStream"));
        } catch (JSONException e) {
          System.out.print(e.getMessage());
        }
        res.add(json);
      }

      model.addAttribute("results", res);
      model.addAttribute("category", category);
      model.addAttribute("sortBy", sortBy);
      model.addAttribute("order", order);
      model.addAttribute("status", status);
      model.addAttribute("keyword", reqParam.get("keyword"));
    }
    return "views/home/tim-kiem";
  }

  @GetMapping(value = "/chi-tiet-dau-gia/{id}")
  public String chi_tiet_dau_gia(@PathVariable long id, Model model)
      throws InterruptedException, ExecutionException {

    Auction auction = auctionRepository.findById(id).get();
    model.addAttribute("auction", auctionRepository.findById(id).get());
    model.addAttribute("category", auction.getCategory());
    Optional<User> user = userRepository.findById(auction.getSeller());
    model.addAttribute("seller", user.get());
    model.addAttribute("auctionRandom", auctionRepository.findLimit6RandomAuction());
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
      model.addAttribute(
          "feedback", feedbackRepository.findFeedbackByUser(optional.get().getId(), id));
    } catch (Exception e) {
      model.addAttribute("feedback", null);
    }
    try {
      model.addAttribute(
          "UsersRegister",
          auctionRegisterRepository.findByAuctionAndIsDeleted(auction, false).get());
    } catch (Exception e) {
      model.addAttribute("UsersRegister", null);
    }
    try {
      model.addAttribute(
          "UserWinner",
          bidRepository
              .findTop1ByAuction_idAndStatusOrderByCreatedDesc(id, "true")
              .get()
              .getUser());
    } catch (Exception e) {
      model.addAttribute("UserWinner", null);
    }
    try {
      model.addAttribute(
          "bidTop",
          bidRepository.findTop1ByAuction_idAndStatusOrderByCreatedDesc(id, "true").get());
    } catch (Exception e) {
      model.addAttribute("bidTop", null);
    }
    model.addAttribute("bids", bidRepository.findTop30ByAuction_idOrderByCreatedDesc(id));
    return "views/home/chi-tiet-dau-gia";
  }

  @GetMapping(value = "/dau-gia-da-tham-gia")
  public String dau_gia_da_tham_gia(Model model) throws InterruptedException, ExecutionException {

    model.addAttribute("auctionRandom", auctionRepository.findLimit6RandomAuction());
    return "views/home/dau-gia-da-tham-gia";
  }

  @GetMapping(value = "/welcome")
  public String welcome() throws InterruptedException, ExecutionException {

    return "welcome";
  }

  @GetMapping(value = "/tai-khoan")
  @ApiOperation(
      value = "getAllAddress",
      authorizations = {@Authorization(value = "JWT")})
  public String tai_khoan(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    try {
      model.addAttribute(
          "register_auction_top_3",
          auctionRegisterRepository
              .findByUserAndIsDeletedAndAuction_status(optional.get(), false, "Upcoming")
              .get());
    } catch (Exception e) {
      model.addAttribute("register_auction_top_3", null);
    }
    return "views/home/tai-khoan";
  }

  @GetMapping(value = "/quen-mat-khau")
  public String quen_mat_khau(Model model) throws InterruptedException, ExecutionException {
    return "views/home/quen-mat-khau";
  }

  @GetMapping(value = "/ho-so-ca-nhan")
  public String ho_so_ca_nhan(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    long id = 0;
    if (optional.isPresent()) id = optional.get().getId();
    Optional<Address> op = addressRepository.findByUserAndIsDefault(id, true);
    if (op.isPresent()) model.addAttribute("addressDefault", op.get());
    if (!optional.isPresent()) return "redirect:";
    return "views/home/ho-so-ca-nhan";
  }

  @GetMapping(value = "/vi-va-so-du")
  @ApiOperation(
      value = "/wallet/transaction",
      authorizations = {@Authorization(value = "JWT")})
  public String vi_va_so_du(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    Long user_id = userRepository.findByEmailAndIsActive((String) principal, true).get().getId();
    model.addAttribute(
        "transaction3",
        transactionRepository.findByFromUserOrToUserOrderByCreatedDesc(user_id, user_id));
    return "views/home/vi-va-so-du";
  }

  @GetMapping(value = "/dat-coc-dau-gia")
  public String dat_coc_dau_gia(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    else
      model.addAttribute(
          "warranties", auctionRegisterRepository.findBiddingAuction(optional.get().getId()));
    return "views/home/dat-coc-dau-gia";
  }

  @GetMapping(value = "/nap-tien")
  public String nap_tien(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/nap-tien";
  }

  @GetMapping(value = "/rut-tien")
  public String rut_tien(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/rut-tien";
  }

  @GetMapping(value = "/rut-tien-buoc2")
  public String rut_tien_buoc2(Model model) throws InterruptedException, ExecutionException {
    return "views/home/rut-tien-buoc2";
  }

  @GetMapping(value = "/rut-tien-buoc3")
  public String rut_tien_buoc3(Model model) throws InterruptedException, ExecutionException {
    return "views/home/rut-tien-buoc3";
  }

  @GetMapping(value = "/dia-chi-van-chuyen")
  @ApiOperation(
      value = "getAllAddress",
      authorizations = {@Authorization(value = "JWT")})
  public String dia_chi_van_chuyen(Model model) throws InterruptedException, ExecutionException {
    try {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
      if (!optional.isPresent()) return "redirect:";
      Long user_id = optional.get().getId();
      model.addAttribute("addresses", addressRepository.findByUser(user_id).get());
    } catch (Exception e) {
      model.addAttribute("addresses", null);
    }
    return "views/home/dia-chi-van-chuyen";
  }

  @GetMapping(value = "/dau-gia-da-mua")
  public String dau_gia_da_mua() throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/dau-gia-da-mua";
  }

  @GetMapping(value = "/dau-gia-da-thang")
  public String dau_gia_da_thang() throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/dau-gia-da-thang";
  }

  @GetMapping(value = "/dau-gia-da-dang-ky")
  public String dau_gia_da_dang_ky(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/dau-gia-da-dang-ky";
  }

  @GetMapping(value = "/dau-gia-dang-tham-du")
  public String dau_gia_dang_tham_du(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/dau-gia-dang-tham-du";
  }

  @GetMapping(value = "/dau-gia-yeu-thich")
  public String dau_gia_yeu_thich(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/dau-gia-yeu-thich";
  }

  @GetMapping(value = "/gio-hang")
  public String gio_hang(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    model.addAttribute(
        "win_auctions", auctionRepository.findByWinnerOrderByEndAtDesc(optional.get().getId()));
    model.addAttribute(
        "paid_auctions",
        auctionRepository.findByWinnerAndStatusOrderByEndAtDesc(optional.get().getId(), "Paid"));
    long id = 0;
    if (optional.isPresent()) id = optional.get().getId();
    Optional<Address> op = addressRepository.findByUserAndIsDefault(id, true);
    if (op.isPresent()) model.addAttribute("addressDefault", op.get());
    return "views/home/gio-hang";
  }

  @GetMapping(value = "/bang-chinh-dai-ly")
  public String bang_chinh_dai_ly(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";

    try {
      User user = userRepository.findByEmailAndIsActive((String) principal, true).get();
      model.addAttribute("AssetTop3", assetRepository.findTop3ByUserId(user.getId()));
    } catch (Exception e) {
      model.addAttribute("AssetTop3", null);
    }
    return "views/home/bang-chinh-dai-ly";
  }

  @GetMapping(value = "/danh-sach-dau-gia")
  public String danh_sach_dau_gia(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/danh-sach-dau-gia";
  }

  @GetMapping(value = "/them-dau-gia/{id}")
  public String them_dau_gia(@PathVariable long id, Model model)
      throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "views/home/dang-nhap";
    model.addAttribute("assets", assetRepository.findById(id).get());
    return "views/home/them-dau-gia";
  }

  @GetMapping(value = "/danh-sach-tai-san")
  public String danh_sach_tai_san(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/danh-sach-tai-san";
  }

  @GetMapping(value = "/them-tai-san")
  public String them_tai_san() throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/them-tai-san";
  }

  @GetMapping(value = "/danh-sach-nguoi-ban")
  public String danh_sach_nguoi_ban() throws InterruptedException, ExecutionException {
    return "views/home/danh-sach-nguoi-ban";
  }

  @GetMapping(value = "/them-nguoi-ban")
  public String them_nguoi_ban() throws InterruptedException, ExecutionException {
    return "views/home/them-nguoi-ban";
  }

  @GetMapping(value = "/ho-so-dai-ly")
  public String ho_so_dai_ly() throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/ho-so-dai-ly";
  }

  @GetMapping(value = "/thay-doi-mat-khau")
  public String thay_doi_mat_khau() throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/thay-doi-mat-khau";
  }

  @GetMapping(value = "/sua-tai-san/{id}")
  public String sua_tai_san(@PathVariable long id, Model model)
      throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    model.addAttribute("asset", assetRepository.findById(id).get());
    return "views/home/sua-tai-san";
  }

  @GetMapping(value = "/AC-Auction/{value}")
  public String tro_giup(Model model, @PathVariable String value)
      throws InterruptedException, ExecutionException {

    model.addAttribute("value", value);
    if (value.contentEquals("tin-tuc")) {
      model.addAttribute("news", newsRepository.findAllByOrderByCreatedDesc());
    }
    return "views/home/AC-Auction/" + value;
  }

  @GetMapping(value = "/tin-tuc")
  public String tin_tuc(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    model.addAttribute("news_1st", newsRepository.findTop1ByOrderByCreatedDesc());
    model.addAttribute("news", newsRepository.findAllByOrderByCreatedDesc());
    model.addAttribute("news_top3", newsRepository.findTop3ByOrderByViewDesc());
    model.addAttribute(
        "auction_is_comming", auctionRepository.findTop2ByStatusOrderByStartAtAsc("Upcoming"));
    model.addAttribute(
        "auction_is_active", auctionRepository.findTop2ByStatusOrderByStartAtAsc("Active"));
    return "views/home/tin-tuc";
  }

  @GetMapping(value = "/tao-tin-tuc")
  public String tao_tin_tuc(Model model) throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    userRepository.findByEmailAndIsActive((String) principal, true);
    //    System.out.println("\n\n\n*************************"+optional.get().getGroup()+
    // "************************\n\n\n");
    if (!optional.get().getGroup().equals("Admin")) return "redirect:";
    model.addAttribute("news", newsRepository.findAllByOrderByCreatedDesc());
    return "views/home/tao-tin-tuc";
  }

  @GetMapping(value = "/chi-tiet-tin-tuc/{alias}")
  public String chi_tiet_tin_tuc(@PathVariable String alias, Model model)
      throws InterruptedException, ExecutionException {
    model.addAttribute("news", newsRepository.findByAlias(alias));
    News entity = newsRepository.findByAlias(alias);
    entity.setView(entity.getView() + 1);
    newsRepository.save(entity);
    model.addAttribute("newsList", newsRepository.findRandomNewsTop8());
    Iterable<News> newslistcheck = newsRepository.findAllByOrderByCreatedDesc();
    News news1st = new News();
    News news2st = new News();
    News news3st = new News();
    boolean founded = false;
    for (News newForEach : newslistcheck) {
      System.out.println(newForEach.getTittle());
      news1st = news2st;
      news2st = news3st;
      news3st = newForEach;
      if (founded == true) break;
      if (newForEach == newsRepository.findByAlias(alias)) {
        founded = true;
      }
    }
    model.addAttribute("news1st", news1st);
    model.addAttribute("news2st", news2st);
    model.addAttribute("news3st", news3st);
    return "views/home/chi-tiet-tin-tuc";
  }

  @GetMapping(value = "/AC-Auction/danh-muc-tin-tuc/{type}")
  public String danh_muc_tin_tuc(@PathVariable String type, Model model)
      throws InterruptedException, ExecutionException {
    model.addAttribute("news", newsRepository.findByType(type));
    return "views/home/AC-Auction/danh-muc-tin-tuc";
  }

  @GetMapping(value = "/verify/register/{email}/{token}")
  public String updateStatus(@PathVariable String email, @PathVariable String token)
      throws InterruptedException, ExecutionException, CustomException {
    try {
      Optional<User> user = userRepository.findByEmailAndIsActive(email, false);
      if (!token.equals(user.get().getCode())) return "Mã xác thực không đúng.";
      user.get().setIsActive(true);
      user.get().setCode("");
      userRepository.save(user.get());
      giftcodeService.createGiftcode(user.get().getId());
      mailService.completedRegisterMail(user.get());
      return "views/home/dang-nhap";
    } catch (Exception e) {
      e.printStackTrace();
      log.warning("verigy register ex: " + e.getMessage());
      Optional<User> user = userRepository.findByEmailAndIsActive(email, true);
      if (user.isPresent()) {
        return "views/home/da-dang-ky";
      } else return "Thông tin xác thực không tồn tại. Vui lòng thực hiện lại thao tác";
    }
  }

  @GetMapping(value = "/verify/notice/{email}/{token}")
  public String updateVerify(@PathVariable String email, @PathVariable String token)
      throws InterruptedException, ExecutionException {
    try {
      Optional<NoticeRegister> noticeRegister =
          noticeRegisterRepository.findByEmailAndIsVerify(email, false);
      if (!token.equals(noticeRegister.get().getVerifycode())) return "Mã xác thực không đúng.";
      noticeRegister.get().setVerify(true);
      noticeRegisterRepository.save(noticeRegister.get());
      return "views/home/dang-nhap";
    } catch (Exception e) {
      return "Thông tin xác thực không tồn tại. Vui lòng thực hiện lại thao tác";
    }
  }

  @GetMapping(value = "/nhap-giftcode")
  public String nhap_giftcode() throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/nhap-giftcode";
  }

  @GetMapping(value = "/google906453a653354218.html")
  public String verify() throws InterruptedException, ExecutionException {
    return "google906453a653354218.html";
  }

  @GetMapping(value = "/google140cafae841c0a9f.html")
  public String verify2() throws InterruptedException, ExecutionException {
    return "/google140cafae841c0a9f.html";
  }

  @GetMapping(value = "/update/information/{email}/{token}")
  public String updateInformation(
      Model model, @PathVariable String email, @PathVariable String token)
      throws InterruptedException, ExecutionException {
    try {

      Optional<User> optional = userRepository.findByEmailAndIsActive(email, true);
      Auction auction = new Auction();
      auction.getAssest().setName("test");
      User user = optional.get();
      mailService.winAuctionMail(user, auction);
      model.addAttribute("user", optional.get());

      return "views/home/cap-nhat-thong-tin";
    } catch (Exception e) {
      e.printStackTrace();
      return "views/home/dang-nhap";
    }
  }

  @GetMapping(value = "/chuong-trinh-affiliate")
  public String chia_se() throws InterruptedException, ExecutionException {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> optional = userRepository.findByEmailAndIsActive((String) principal, true);
    if (!optional.isPresent()) return "redirect:";
    return "views/home/chia-se";
  }
}
