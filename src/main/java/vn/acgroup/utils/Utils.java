package vn.acgroup.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Utils {
  public static LocalDateTime setTimeZone(LocalDateTime localDateTime) {
    return localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime();
  }

  public static long hexToDecimal(String hex) {
    long decimal = Long.parseLong(hex, 16);
    return decimal;
  }

  public static String decimalToHex(long decimal) {
    return Long.toHexString(decimal);
  }
}
