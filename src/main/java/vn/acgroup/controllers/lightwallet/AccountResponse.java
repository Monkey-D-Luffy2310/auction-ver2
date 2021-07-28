package vn.acgroup.controllers.lightwallet;

import java.util.List;

public class AccountResponse {

  private String id;
  private String email;
  private Integer createdAt;
  private Integer updatedAt;
  private String currency;
  private String balance;
  private String resourcePath;
  private String resource;
  private Boolean primary;
  private String type;
  private String name;
  private String primaryAddress;
  private String secretSeeds;
  private Object status;
  private List<Object> tokenBook = null;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Integer getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Integer createdAt) {
    this.createdAt = createdAt;
  }

  public Integer getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Integer updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getBalance() {
    return balance;
  }

  public void setBalance(String balance) {
    this.balance = balance;
  }

  public String getResourcePath() {
    return resourcePath;
  }

  public void setResourcePath(String resourcePath) {
    this.resourcePath = resourcePath;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public Boolean getPrimary() {
    return primary;
  }

  public void setPrimary(Boolean primary) {
    this.primary = primary;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPrimaryAddress() {
    return primaryAddress;
  }

  public void setPrimaryAddress(String primaryAddress) {
    this.primaryAddress = primaryAddress;
  }

  public String getSecretSeeds() {
    return secretSeeds;
  }

  public void setSecretSeeds(String secretSeeds) {
    this.secretSeeds = secretSeeds;
  }

  public Object getStatus() {
    return status;
  }

  public void setStatus(Object status) {
    this.status = status;
  }

  public List<Object> getTokenBook() {
    return tokenBook;
  }

  public void setTokenBook(List<Object> tokenBook) {
    this.tokenBook = tokenBook;
  }
}
