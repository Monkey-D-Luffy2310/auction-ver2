package vn.acgroup.service.wallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import vn.acgroup.config.LightwalletConfig;
import vn.acgroup.config.VndtConfig;
import vn.acgroup.controllers.api.WithdrawRequest;
import vn.acgroup.controllers.api.WithdrawToBankRequest;
import vn.acgroup.controllers.lightwallet.Account;
import vn.acgroup.controllers.lightwallet.AccountResponse;
import vn.acgroup.controllers.lightwallet.Data;
import vn.acgroup.controllers.lightwallet.Notification;
import vn.acgroup.controllers.lightwallet.Withdraw;
import vn.acgroup.controllers.lightwallet.WithdrawResponse;
import vn.acgroup.controllers.vndt.BankInfomation;
import vn.acgroup.controllers.vndt.ImportHashRequest;
import vn.acgroup.entities.SavedBank;
import vn.acgroup.entities.Transaction;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.NoticeRegisterRepository;
import vn.acgroup.repositories.SavedBankRepository;
import vn.acgroup.repositories.TransactionRepository;
import vn.acgroup.repositories.UserRepository;
import vn.acgroup.service.giftcode.GiftcodeService;
import vn.acgroup.service.mqtt.MqttService;

@Service
public class LightWalletService implements WalletService {
  @Autowired TransactionRepository transactionRepository;
  @Autowired RestTemplate lightWalletRestTemplate;
  @Autowired UserRepository userRepository;
  @Autowired NoticeRegisterRepository noticeRegisterRepository;
  @Autowired GiftcodeService giftcodeService;
  @Autowired SavedBankRepository savedBankRepository;
  @Autowired MqttService mqttService;

  Logger logger = Logger.getLogger(this.getClass().getName());

  @Autowired
  @Qualifier("bonusWalletTemplate")
  RestTemplate bonusWalletTemplate;

  public long sendVNDT(@PathVariable User user, @PathVariable WithdrawRequest withdrawRequest) {
    if (withdrawRequest.getAmount().compareTo(user.getVNDTBalance()) > 0) return 404;

    Withdraw withdraw = new Withdraw(withdrawRequest);

    String url = LightwalletConfig.URL + "/admin@daugia247.net|" + user.getId() + "/transactions";
    HttpEntity<Withdraw> request = new HttpEntity<>(withdraw);
    WithdrawResponse result =
        lightWalletRestTemplate.postForObject(url, request, WithdrawResponse.class);

    Transaction transaction = new Transaction(user.getId(), result);
    transaction.setFromAddress(user.getWalletAddress());
    userRepository
        .findByWalletAddress(result.getTo())
        .ifPresent((toUser) -> transaction.setToUser(toUser.getId()));
    transactionRepository.save(transaction);

    if (result.getType().equals("VNDT")) {
      user.setVNDTBalance(user.getVNDTBalance().subtract(result.getAmount()));
    }

    userRepository.save(user);
    return 200;
  }

  public List<SavedBank> getDepositBank() {
    try {
      String result =
          Jsoup.connect("https://vndtltc.appspot.com/api/get-default-receive-bank")
              .ignoreContentType(true)
              .ignoreHttpErrors(true)
              .get()
              .body()
              .text();
      logger.info("get bank result: " + result);
      JSONArray banks = new JSONArray(result);
      List<SavedBank> listbanks = new ArrayList<SavedBank>();
      for (int i = 0; i < banks.length(); i++) {
        JSONObject json = banks.getJSONObject(i);
        SavedBank b =
            new SavedBank(
                json.getString("accountNumber"),
                json.getString("accountName"),
                "",
                json.getString("bankName"));
        listbanks.add(b);
      }
      return listbanks;
    } catch (Exception e) {
      return null;
    }
  }

  // public static void main(String[] args) {
  // LightWalletService service = new LightWalletService();
  // service.getDepositBank();
  // }

  public long activeWallet(@PathVariable String address) {

    WithdrawRequest withdrawRequest = new WithdrawRequest(address, BigDecimal.valueOf(0.1), "test");
    Withdraw withdraw = new Withdraw(withdrawRequest);
    withdraw.setCurrency("TRX");
    String url = LightwalletConfig.URL + "/admin@daugia.io|" + 10002 + "/transactions";
    HttpEntity<Withdraw> request = new HttpEntity<>(withdraw);
    WithdrawResponse result =
        bonusWalletTemplate.postForObject(url, request, WithdrawResponse.class);

    Transaction transaction = new Transaction(10002, result);
    transaction.setFromAddress("TRugC2hcixbonCNZjbmKjcbtNgK3x7vNUR");
    transaction.setNote("Kích hoạt ví");
    userRepository
        .findByWalletAddress(result.getTo())
        .ifPresent((toUser) -> transaction.setToUser(toUser.getId()));
    transactionRepository.save(transaction);

    return 200;
  }

  public long sendBonus(@PathVariable User user, @PathVariable WithdrawRequest withdrawRequest) {
    if (withdrawRequest.getAmount().compareTo(user.getBonusBalance()) > 0) {
      logger.info("withdrawRequest.getAmount: " + withdrawRequest.getAmount());
      logger.info("user.getBonusBalance: " + user.getBonusBalance());
      return 403;
    }

    Withdraw withdraw = new Withdraw(withdrawRequest);
    withdraw.setTo(withdrawRequest.getTo());
    withdraw.setAmount(withdrawRequest.getAmount());
    withdraw.setDescription(withdrawRequest.getDescription());

    String url = LightwalletConfig.URL + "/admin@daugia.io|" + user.getId() + "/transactions";
    HttpEntity<Withdraw> request = new HttpEntity<>(withdraw);
    WithdrawResponse result =
        bonusWalletTemplate.postForObject(url, request, WithdrawResponse.class);

    Transaction transaction = new Transaction(user.getId(), result);
    transaction.setFromAddress(user.getBonusAddress());
    userRepository
        .findByBonusAddress(result.getTo())
        .ifPresent((toUser) -> transaction.setToUser(toUser.getId()));
    transactionRepository.save(transaction);

    user.setBonusBalance(user.getBonusBalance().subtract(result.getAmount()));
    userRepository.save(user);
    return 200;
  }

  public void updateTransaction(@PathVariable Notification notifi) {

    String hash = notifi.getData().getHash();
    Optional<Transaction> transaction = transactionRepository.findByHash(hash);
    if (transaction.isPresent()) {
      if (transaction.get().getToUser() != 0 && transaction.get().getStatus().equals("Pending")) {
        Long id = transaction.get().getToUser();
        User user = userRepository.findById(id).get();
        BigDecimal amount = new BigDecimal(notifi.getData().getAmount());
        if (notifi.getNetwork().equals("VNDT")) {
          user.setVNDTBalance(user.getVNDTBalance().add(amount));
        }
        userRepository.save(user);
      }
      transaction.get().setStatus(notifi.getData().getStatus());
      transactionRepository.save(transaction.get());
    } else {
      Data data = notifi.getData();
      long toUser = userRepository.findByWalletAddress(data.getToAddress()).get().getId();

      Transaction newTransaction = new Transaction(toUser, data);
      transactionRepository.save(newTransaction);

      User user = userRepository.findByWalletAddress(data.getToAddress()).get();
      if (notifi.getNetwork().equals("VNDT")) {
        user.setVNDTBalance(user.getVNDTBalance().add(newTransaction.getAmount()));
      }

      HashMap<String, String> val = new HashMap<String, String>();
      val.put("userId", user.getId() + "");
      try {
        mqttService.sendToMqtt(val, "auction/balance");
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      userRepository.save(user);
    }
  }

  public void updateTransactionBonus(@PathVariable Notification notifi) {

    String hash = notifi.getData().getHash();
    Optional<Transaction> transaction = transactionRepository.findByHash(hash);
    if (transaction.isPresent()) {
      if (transaction.get().getToUser() != 0 && transaction.get().getStatus().equals("Pending")) {
        Long id = transaction.get().getToUser();
        User user = userRepository.findById(id).get();
        BigDecimal amount = new BigDecimal(notifi.getData().getAmount());
        if (notifi.getNetwork().equals("VNDT")) {
          user.setBonusBalance((user.getBonusBalance().add(amount)));
        }
        userRepository.save(user);
      }
      transaction.get().setStatus(notifi.getData().getStatus());
      transactionRepository.save(transaction.get());
    } else {
      Data data = notifi.getData();
      User user = userRepository.findByBonusAddress(data.getToAddress()).get();

      Transaction newTransaction = new Transaction(user.getId(), data);
      transactionRepository.save(newTransaction);

      if (notifi.getNetwork().equals("VNDT")) {
        user.setBonusBalance((user.getBonusBalance().add(newTransaction.getAmount())));
      }

      HashMap<String, String> val = new HashMap<String, String>();
      val.put("userId", user.getId() + "");
      try {
        mqttService.sendToMqtt(val, "auction/balance");
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      userRepository.save(user);
    }
  }

  public String resetBalance(@PathVariable long id) {
    User user = userRepository.findById(id).get();
    if (user.getWalletAddress() != null) {
      String url =
          LightwalletConfig.URL
              + "/admin@daugia247.net|"
              + id
              + "/address/"
              + user.getWalletAddress()
              + "/check-balance/VNDT";
      try {
        String result = lightWalletRestTemplate.getForObject(url, String.class);
        String s = result.split(":")[1];
        BigDecimal wallet = new BigDecimal(s.substring(0, s.length() - 1));
        user.setVNDTBalance(wallet.subtract(user.getVNDTFreeze()));
      } catch (Exception e) {
        user.setVNDTBalance(BigDecimal.ZERO);
      }
      userRepository.save(user);
    }
    return "OK";
  }

  public String resetBonusBalance(@PathVariable long id) {
    User user = userRepository.findById(id).get();
    if (user.getWalletAddress() != null) {
      String url =
          LightwalletConfig.URL
              + "/admin@daugia.io|"
              + id
              + "/address/"
              + user.getBonusAddress()
              + "/check-balance/VNDT";
      try {
        String result = bonusWalletTemplate.getForObject(url, String.class);
        String s = result.split(":")[1];
        BigDecimal wallet = new BigDecimal(s.substring(0, s.length() - 1));
        user.setBonusBalance(wallet);
      } catch (Exception e) {
        user.setBonusBalance(BigDecimal.ZERO);
      }
      userRepository.save(user);
    }
    return "OK";
  }

  public void createBonusWallet(@PathVariable long id) {
    User user = userRepository.findById(id).get();
    Account account = new Account();
    account.setName(user.getId() + "");
    account.setCurrency("TRX");
    account.setPrimary(true);
    HttpEntity<Account> request = new HttpEntity<>(account);
    AccountResponse result =
        bonusWalletTemplate.postForObject(LightwalletConfig.URL, request, AccountResponse.class);
    user.setBonusAddress(result.getPrimaryAddress());
    userRepository.save(user);
  }

  public void createProductWallet(@PathVariable long id) {
    User user = userRepository.findById(id).get();
    Account account = new Account();
    account.setName(user.getId() + "");
    account.setCurrency("TRX");
    account.setPrimary(true);
    HttpEntity<Account> request = new HttpEntity<>(account);
    AccountResponse result =
        lightWalletRestTemplate.postForObject(
            LightwalletConfig.URL, request, AccountResponse.class);
    user.setWalletAddress(result.getPrimaryAddress());
    userRepository.save(user);
  }

  public long transfer(@PathVariable User user, @PathVariable BigDecimal amount) {
    if (amount.compareTo(user.getVNDTBalance()) > 0) return 404;
    WithdrawRequest withdrawRequest =
        new WithdrawRequest(
            user.getBonusAddress(), amount, "Chuyển tiền từ tài khoản chính sang tài khoản phụ");
    if (sendVNDT(user, withdrawRequest) == 200) {
      user.setBonusBalance(user.getBonusBalance().add(amount));
      userRepository.save(user);
      giftcodeService.bonus(user.getId(), amount);
      return 200;
    }
    return 404;
  }

  public long withdrawVndtToBank(User user, WithdrawToBankRequest withdrawRequest) {
    if (withdrawRequest.getAmount().compareTo(user.getVNDTBalance()) > 0) return 404;

    Withdraw withdraw = new Withdraw();
    withdraw.setTo(VndtConfig.MAIN_ADDRESS);
    withdraw.setAmount(withdrawRequest.getAmount());
    withdraw.setDescription(withdrawRequest.getDescription());

    String url = LightwalletConfig.URL + "/admin@daugia247.net|" + user.getId() + "/transactions";
    HttpEntity<Withdraw> request = new HttpEntity<>(withdraw);
    WithdrawResponse result =
        lightWalletRestTemplate.postForObject(url, request, WithdrawResponse.class);

    Transaction transaction = new Transaction(user, result);
    transaction.setFromAddress(user.getWalletAddress());
    userRepository
        .findByWalletAddress(result.getTo())
        .ifPresent((toUser) -> transaction.setToUser(toUser.getId()));
    transactionRepository.save(transaction);

    importHashToVndt(
        "ACA" + transaction.getId(),
        transaction.getHash(),
        withdrawRequest.getAccountNumber(),
        withdrawRequest.getAccountName(),
        withdrawRequest.getBankCode(),
        withdraw.getDescription());

    if (result.getType().equals("VNDT")) {
      user.setVNDTBalance(user.getVNDTBalance().subtract(result.getAmount()));
    }

    userRepository.save(user);
    return 200;
  }

  public void importHashToVndt(
      String orderId,
      String hash,
      String accountNumber,
      String accountName,
      String bankCode,
      String note) {
    try {
      BankInfomation info = new BankInfomation(orderId, bankCode, accountNumber, accountName, note);
      ImportHashRequest request = new ImportHashRequest();
      request.setHash(hash);
      request.setId(VndtConfig.ACA_ID);
      request.setToken(VndtConfig.TOKEN);
      request.setInfo(info);
      String url = "https://vndtltc.appspot.com/api/import-hash-v2";
      Response response =
          Jsoup.connect(url)
              .ignoreContentType(true)
              .ignoreHttpErrors(true)
              .header("Content-Type", "application/json")
              .requestBody(request.toString())
              .method(Method.POST)
              .execute();
      logger.info(response.body());
    } catch (Exception e) {
      logger.info("Import hash error: " + e.getMessage());
    }
    return;
  }

  public SavedBank getBankAccountInfo(String accountNumber, String bankCode)
      throws CustomException {
    Optional<SavedBank> bankOptionl = savedBankRepository.findById(accountNumber);
    if (bankOptionl.isPresent()) {
      logger.info("bank save! get bank from db");
      return bankOptionl.get();
    }
    logger.info("bank not found, start get in VNDT API");
    try {
      String url =
          "https://vndtltc.appspot.com/api/inquiry/account/number/"
              + accountNumber
              + "/bank/"
              + bankCode;

      Document doc =
          Jsoup.connect(url)
              .ignoreContentType(true)
              .ignoreHttpErrors(true)
              .header("Content-Type", "application/json")
              .timeout(90000)
              .get();
      System.out.println(doc.body().text());

      JSONObject res = new JSONObject(doc.body().text());
      boolean status = res.getBoolean("status");
      if (!status) {
        throw new CustomException("Account number invalid", HttpStatus.FORBIDDEN.value());
      }
      String accountName = res.getString("data");
      if (accountName == null || accountName.equals("")) {
        throw new CustomException("Load bank account fail", HttpStatus.FORBIDDEN.value());
      }

      if (accountName.indexOf("VND-TGTT-") > -1) {
        accountName = accountName.replace("VND-TGTT-", "");
      }

      SavedBank bank =
          new SavedBank(accountNumber, accountName, bankCode, getBankNameFromCode(bankCode));
      savedBankRepository.save(bank);
      return bank;
    } catch (Exception e) {
      // TODO Auto-generated catch block
      logger.warning(e.getMessage());
      throw new CustomException("Load bank account fail", HttpStatus.FORBIDDEN.value());
    }
  }

  public String getBankNameFromCode(String code) {
    HashMap<String, String> listBank = new HashMap<>();
    listBank.put("422589", "CIMB");
    listBank.put("970400", "SAIGONBANK");
    listBank.put("970403", "SACOMBANK");
    listBank.put("970405", "AGRIBANK");
    listBank.put("970406", "DONGABANK");
    listBank.put("970408", "GPB");
    listBank.put("970412", "PVCOMBANK");
    listBank.put("970414", "OCEANBANK");
    listBank.put("970415", "VIETINBANK");
    listBank.put("970416", "ACB");
    listBank.put("970418", "BIDV");
    listBank.put("970419", "NCB");
    listBank.put("970421", "VRB");
    listBank.put("970422", "MB BANK"); // MB- quan doi
    listBank.put("970423", "TPBANK");
    listBank.put("970424", "SHBVN");
    listBank.put("970425", "ABBANK");
    listBank.put("970426", "MSB");
    listBank.put("970427", "VAB");
    listBank.put("970428", "NAMABANK");
    listBank.put("970429", "SCB");
    listBank.put("970430", "PG BANK");
    listBank.put("970432", "VPBANK");
    listBank.put("970433", "VIETBANK");
    listBank.put("970434", "INDOVINA");
    listBank.put("970436", "VIETCOMBANK");
    listBank.put("970437", "HDB");
    listBank.put("970438", "BVB");
    listBank.put("970439", "PBVN");
    listBank.put("970440", "SEABANK");
    listBank.put("970441", "VIB");
    listBank.put("970442", "HONGLEONG");
    listBank.put("970443", "SHB");
    listBank.put("970448", "OCB");
    listBank.put("970449", "LPB");
    listBank.put("970452", "KIENLONGBANK");
    listBank.put("970454", "VIETCAPITAL");
    listBank.put("970455", "IBK");
    listBank.put("970457", "WO");
    listBank.put("970458", "UOB");
    listBank.put("970407", "TCB");
    if (listBank.containsKey(code)) {
      return listBank.get(code);
    } else return null;
  }

  public String getBankCodeFromName(String name) {
    HashMap<String, String> listBank = new HashMap<>();
    listBank.put("CIMB", "422589");
    listBank.put("SAIGONBANK", "970400");
    listBank.put("SACOMBANK", "970403");
    listBank.put("AGRIBANK", "970405");
    listBank.put("DONGABANK", "970406");
    listBank.put("GPB", "970408");
    listBank.put("PVCOMBANK", "970412");
    listBank.put("OCEANBANK", "970414");
    listBank.put("VIETINBANK", "970415");
    listBank.put("ACB", "970416");
    listBank.put("BIDV", "970418");
    listBank.put("NCB", "970419");
    listBank.put("VRB", "970421");
    listBank.put("MB BANK", "970422");
    listBank.put("TPBANK", "970423");
    listBank.put("SHBVN", "970424");
    listBank.put("ABBANK", "970425");
    listBank.put("MSB", "970426");
    listBank.put("VAB", "970427");
    listBank.put("NAMABANK", "970428");
    listBank.put("SCB", "970429");
    listBank.put("PG BANK", "970430");
    listBank.put("VPBANK", "970432");
    listBank.put("VIETBANK", "970433");
    listBank.put("INDOVINA", "970434");
    listBank.put("VIETCOMBANK", "970436");
    listBank.put("HDB", "970437");
    listBank.put("BVB", "970438");
    listBank.put("PBVN", "970439");
    listBank.put("SEABANK", "970440");
    listBank.put("VIB", "970441");
    listBank.put("HONGLEONG", "970442");
    listBank.put("SHB", "970443");
    listBank.put("OCB", "970448");
    listBank.put("LPB", "970449");
    listBank.put("KIENLONGBANK", "970452");
    listBank.put("VIETCAPITAL", "970454");
    listBank.put("IBK", "970455");
    listBank.put("WO", "970457");
    listBank.put("UOB", "970458");
    listBank.put("TCB", "970407");
    if (listBank.containsKey(name)) {
      return listBank.get(name);
    } else return null;
  }
}