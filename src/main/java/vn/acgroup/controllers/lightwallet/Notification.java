package vn.acgroup.controllers.lightwallet;

import java.util.UUID;

public class Notification {

  private String id;
  private String type;
  private Data data;
  private long createdAt;
  private String network;
  private String additionData;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public String getNetwork() {
    return network;
  }

  public void setNetwork(String network) {
    this.network = network;
  }

  public String getAdditionData() {
    return additionData;
  }

  public void setAdditionData(String additionData) {
    this.additionData = additionData;
  }

  public String generateId() {
    return UUID.randomUUID().toString();
  }

  public Notification() {}

  public Notification(String type, Data data, String network, String additionData) {
    this.id = generateId();
    this.type = type;
    this.data = data;
    this.createdAt = System.currentTimeMillis();
    this.network = network;
    this.additionData = additionData;
  }

  //	public static Notification createSendNotification(String amount, String fee, String from,
  // String to, String hash,
  //			String type, String network, String status, String additionData) {
  //		Data data = new Data(from, from, to, hash, amount, fee, status);
  //		Notification notification = new Notification(type, data, network);
  //		return notification;
  //	}

  public static Notification createReceiveNotification(
      String amount,
      String fee,
      String from,
      String to,
      String hash,
      String type,
      String network,
      String status,
      String additionData) {
    Data data = new Data(to, from, to, hash, amount, fee, status);
    Notification notification = new Notification(type, data, network, additionData);
    return notification;
  }
}
