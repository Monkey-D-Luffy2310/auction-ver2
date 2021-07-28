package vn.acgroup.service.report;

import vn.acgroup.entities.User;
import vn.acgroup.exception.CustomException;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.TransactionRepository;
import vn.acgroup.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

  @Autowired AuctionRepository auctionRepository;

  @Autowired UserRepository userRepository;

  @Autowired AuctionRegisterRepository auctionRegisterRepository;

  @Autowired TransactionRepository transactionRepository;

  public ArrayList<HashMap<String, Object>> report() throws CustomException {
    ArrayList<HashMap<String, Object>> reports = new ArrayList<HashMap<String, Object>>();
    try {
      auctionRepository
          .findByStatus("Ended")
          .forEach(
              auc -> {
                HashMap<String, Object> report = new HashMap<String, Object>();
                if (auctionRegisterRepository.findByAuction(auc).isPresent()) {
                  BigDecimal ticket =
                      new BigDecimal(auctionRegisterRepository.findTurnByAuction_Id(auc.getId()))
                          .multiply(auc.getRegistrationFee());
                  BigDecimal warranty = auc.getWarranty();
                  BigDecimal rest = auc.getCurrentPrice().subtract(auc.getWarranty());
                  report.put("id", auc.getId());
                  report.put("type", auc.getType());
                  report.put("startAt", auc.getStartAt().toString());
                  report.put("category", auc.getCategory());
                  report.put("name", auc.getAssest().getName());
                  report.put("ticket", ticket);
                  report.put("warranty", warranty);
                  report.put("rest", rest);
                  report.put("sum", ticket.add(warranty).add(rest));
                }
                if (!report.isEmpty()) reports.add(report);
              });
    } catch (Exception e) {
      throw new CustomException(e.getMessage(), HttpStatus.FORBIDDEN.value());
    }
    return reports;
  }

  public ArrayList<HashMap<String, Object>> statistical(String startAt) throws CustomException {
    ArrayList<HashMap<String, Object>> statistics = new ArrayList<HashMap<String, Object>>();

    auctionRepository
        .statistical(startAt)
        .forEach(
            auc -> {
              HashMap<String, Object> statistic = new HashMap<String, Object>();
              User user = userRepository.findById(auc.getWinner()).get();
              statistic.put("id", auc.getId());
              statistic.put("name", user.getFullname() + user.getLastname());
              statistic.put("username", user.getName());
              statistic.put("mobile", user.getMobile());
              statistic.put("email", user.getEmail());
              statistic.put("asset", auc.getAssest().getName());
              statistic.put("address", user.getProvince());
              statistic.put("winPrice", auc.getWinPrice());
              statistic.put(
                  "deposit",
                  auctionRegisterRepository
                      .findByUser_IdAndAuction_Id(auc.getWinner(), auc.getId())
                      .get()
                      .getWarranty());
              if (auc.getStatus().equals("Paid")) statistic.put("status", "Đã thanh toán");
              else statistic.put("status", "Chưa thanh toán");
              statistics.add(statistic);
            });

    return statistics;
  }

  public List<Map<String, Object>> reportAuction(String startAt) throws CustomException {
    return auctionRepository.reportAuctionActive(startAt.replaceAll("%", ""));
  }

  public Map<String, Object> reportUser(String startAt, String endAt, long userId)
      throws CustomException {

    Map<String, Object> dataResp = new HashMap<String, Object>();

    User user = userRepository.findById(userId).get();
    List<Map<String, Object>> tr =
        transactionRepository.findByCreatedAndUser(startAt, endAt, userId);
    List<Map<String, Object>> au = auctionRepository.findByCreatedAndUser(startAt, endAt, userId);

    user.setPassword("");
    user.setResetToken("");

    dataResp.put("user", user);
    dataResp.put("trans", tr);
    dataResp.put("auction", au);
    return dataResp;
  }

  public Map<String, Object> reportAuctionDetail(long auctionId) throws CustomException {
    Map<String, Object> obj = new HashMap<String, Object>();
    List<Map<String, Object>> au = auctionRepository.findByAuctionId(auctionId);
    if (au.size() > 0) obj = au.get(0);
    return obj;
  }
}
