package vn.acgroup.exception;

public class CustomException extends Exception {
  private String message;
  private Integer code;

  public String getMessage() {
    return message;
  }

  public Integer getCode() {
    return code;
  }

  public CustomException(String message, Integer code) {
    super();
    this.message = message;
    this.code = code;
  }
}
