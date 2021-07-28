package vn.acgroup.controllers.mobile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import vn.acgroup.exception.CustomException;
import vn.acgroup.service.auction.AuctionService;

@RestController
public class MobileAuctionController {

  @Autowired AuctionService auctionService;

  @GetMapping(value = "mobile/auction")
  @ResponseBody
  public ResponseEntity getAuctionById(@RequestParam Map<String, String> reqParam)
      throws InterruptedException, ExecutionException {
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    long limit = 100;
    try {
      if (reqParam.get("limit") != null) {
        limit = Long.parseLong(reqParam.get("limit"));
      }
      list = auctionService.findAuctionByStatus(reqParam.get("status"), limit);
    } catch (CustomException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<>(list, HttpStatus.OK);
  }
}
