package vn.acgroup.service.wallet;

import java.math.BigDecimal;
import java.util.List;

import vn.acgroup.controllers.api.WithdrawRequest;
import vn.acgroup.controllers.api.WithdrawToBankRequest;
import vn.acgroup.controllers.lightwallet.Notification;
import vn.acgroup.entities.SavedBank;
import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;

public interface WalletService {

  public long sendVNDT(User user, WithdrawRequest withdrawRequest);

  public long withdrawVndtToBank(User user, WithdrawToBankRequest request);

  public long sendBonus(User user, WithdrawRequest withdrawRequest);

  public void updateTransaction(Notification notifi);

  public void updateTransactionBonus(Notification notifi);

  //  public BigDecimal maxWithdraw(long id);
  public String resetBalance(long id);

  public String resetBonusBalance(long id);

  public void createBonusWallet(long user_id);

  public void createProductWallet(long user_id);

  public long transfer(User user, BigDecimal amount);

  public long activeWallet(String address);

  public List<SavedBank> getDepositBank();

  public SavedBank getBankAccountInfo(String accountNumber, String bankCode) throws CustomException;
  
  public String getBankNameFromCode(String code);
  
  public String getBankCodeFromName(String name);
  
}