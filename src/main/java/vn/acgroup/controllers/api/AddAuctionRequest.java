package vn.acgroup.controllers.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AddAuctionRequest {
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private long assest;
  private BigDecimal bidPrice;
  private BigDecimal buyPrice;
  private BigDecimal warranty;
  private BigDecimal stepPrice;

  private String area;
  private long seller = 0;
  private LocalDateTime attendanceDeadline;
  private String note = "";
  private String regulation;
  private String type;
  private int percent;
  private BigDecimal registrationFee;
  private Integer attendingUser;
  private double sellOffPercent;

  public Integer getAttendingUser() {
    return attendingUser;
  }

  public void setAttendingUser(Integer attendingUser) {
    this.attendingUser = attendingUser;
  }

  public BigDecimal getRegistrationFee() {
    return registrationFee;
  }

  public void setRegistrationFee(BigDecimal registrationFee) {
    this.registrationFee = registrationFee;
  }

  public int getPercent() {
    return percent;
  }

  public void setPercent(int percent) {
    this.percent = percent;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getRegulation() {
    return regulation;
  }

  public void setRegulation(String regulation) {
    this.regulation = regulation;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public long getSeller() {
    return seller;
  }

  public void setSeller(long seller) {
    this.seller = seller;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public LocalDateTime getAttendanceDeadline() {
    return attendanceDeadline;
  }

  public void setAttendanceDeadline(LocalDateTime attendanceDeadline) {
    this.attendanceDeadline = attendanceDeadline;
  }

  public BigDecimal getStepPrice() {
    return stepPrice;
  }

  public void setStepPrice(BigDecimal stepPrice) {
    this.stepPrice = stepPrice;
  }

  public long getAssest() {
    return assest;
  }

  public void setAssest(long assest) {
    this.assest = assest;
  }

  public LocalDateTime getStartAt() {
    return startAt;
  }

  public void setStartAt(LocalDateTime startAt) {
    this.startAt = startAt;
  }

  public LocalDateTime getEndAt() {
    return endAt;
  }

  public void setEndAt(LocalDateTime endAt) {
    this.endAt = endAt;
  }

  public BigDecimal getBidPrice() {
    return bidPrice;
  }

  public void setBidPrice(BigDecimal bidPrice) {
    this.bidPrice = bidPrice;
  }

  public BigDecimal getWarranty() {
    return warranty;
  }

  public void setWarranty(BigDecimal warranty) {
    this.warranty = warranty;
  }

  public BigDecimal getBuyPrice() {
    return buyPrice;
  }

  public void setBuyPrice(BigDecimal buyPrice) {
    this.buyPrice = buyPrice;
  }

  public double getSellOffPercent() {
    return sellOffPercent;
  }

  public void setSellOffPercent(double sellOffPercent) {
    this.sellOffPercent = sellOffPercent;
  }
}
