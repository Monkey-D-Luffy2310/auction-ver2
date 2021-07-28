package vn.acgroup.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import vn.acgroup.exception.CustomException;
import vn.acgroup.service.report.ReportService;

@Controller
@CrossOrigin(origins = "*")
public class ReportController {
  @Autowired ReportService reportService;

  @GetMapping(value = "/admin/report")
  @ResponseBody
  @ApiOperation(
      value = "report",
      authorizations = {@Authorization(value = "JWT")})
  public ArrayList<HashMap<String, Object>> report()
      throws InterruptedException, ExecutionException, CustomException {
    return reportService.report();
  }

  @GetMapping(value = "/admin/statistical")
  @ResponseBody
  @ApiOperation(
      value = "statistical",
      authorizations = {@Authorization(value = "JWT")})
  public List<Map<String, Object>> statistical(String startAt)
      throws InterruptedException, ExecutionException, CustomException {
    return reportService.reportAuction(startAt);
  }

  @PostMapping(value = "/admin/user-info")
  @ResponseBody
  @ApiOperation(
      value = "user-detail",
      authorizations = {@Authorization(value = "JWT")})
  public Map<String, Object> userInfo(@RequestBody Map<String, Object> reqBody)
      throws InterruptedException, ExecutionException, CustomException {
    try {
      String startAt = reqBody.get("startAt").toString();
      String endAt = reqBody.get("endAt").toString();
      long userId = Long.parseLong(reqBody.get("userId").toString());
      return reportService.reportUser(startAt, endAt, userId);
    } catch (Exception e) {
      return null;
    }
  }

  @PostMapping(value = "/admin/auction-detail")
  @ResponseBody
  @ApiOperation(
      value = "auction-detail",
      authorizations = {@Authorization(value = "JWT")})
  public Map<String, Object> auctionDetail(@RequestBody Map<String, Object> reqBody)
      throws InterruptedException, ExecutionException, CustomException {
    long auctionId = Long.parseLong(reqBody.get("auctionId").toString());
    return reportService.reportAuctionDetail(auctionId);
  }
}
