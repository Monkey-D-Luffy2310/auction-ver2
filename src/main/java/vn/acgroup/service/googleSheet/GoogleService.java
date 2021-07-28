package vn.acgroup.service.googleSheet;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.gson.Gson;

import vn.acgroup.entities.Address;
import vn.acgroup.entities.Auction;
import vn.acgroup.entities.AuctionRegister;
import vn.acgroup.entities.User;
import vn.acgroup.repositories.AddressRepository;
import vn.acgroup.repositories.AuctionRegisterRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.UserRepository;

@Service
public class GoogleService {

  @Autowired UserRepository userRepository;
  @Autowired AddressRepository addressRepository;

  @Autowired AuctionRepository auctionRepository;
  @Autowired AuctionRegisterRepository auctionRegisterRepository;

  static Logger log = Logger.getLogger(GoogleService.class.getName());
  public static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
  public static final String aToZ = " abcdefghijklmnopqrstuvwxzy".toUpperCase();
  public static final String AuctionSheetId = "1YL-34q4syHAJ9pTMFjzJU8DDZuimVI_WIER3L6X0Wmw";

  public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");

  public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {
    // Load client secrets.
    // Build flow and trigger user authorization request.
    GoogleCredential credential =
        GoogleCredential.fromStream(
                GoogleService.class.getResourceAsStream("/acwallet-918b692ce5dd.json"))
            .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
    return credential;
  }

  //  	public static void main(String[] args) {
  //  	  Auction auction = new Auction();
  //	  auction.setCreated(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
  //	  auction.setId(12345);
  //
  //	  Asset asset = new Asset();
  //	  asset.setId(123);
  //	  asset.setName("Abcd Name");
  //	  auction.setAssest(asset);
  //	  auction.setBuyPrice(new BigDecimal("1000000"));
  //	  auction.setBidPrice(new BigDecimal("121321"));
  //	  auction.setWinPrice(new BigDecimal("5555555"));
  //
  //
  //	  List<Bid> bids =  new ArrayList<Bid>();
  //	  auction.setBids(bids);
  //	  auction.setRegistrationFee(new BigDecimal(10000));
  //	  auction.setAttendingUser(11);
  //
  //
  //	  User winner = new User();
  //	  winner.setId(12);
  //	  winner.setName("Anh day");
  //	  winner.setEmail("anhday@gmail.com");
  //
  //	  Address address=  new Address();
  //
  //	  address.setMobile("0987654321");
  //	  address.setAddress("anh da tung di dai o day");
  //
  //	  AuctionRegister auctionRegister = new AuctionRegister();
  //	  auctionRegister.setWarranty(new BigDecimal(15200));
  //	  System.out.println("start exposrt data");
  //	  exportAuctionToGoogleSheet(auction, winner, auctionRegister, address);
  //  	}

  public void exportAuctionToGoogleSheet(
      Auction auction, int bids, User winner, AuctionRegister auctionRegister, Address address) {
    try {
      NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      Sheets service =
          new Sheets.Builder(
                  HTTP_TRANSPORT,
                  JacksonFactory.getDefaultInstance(),
                  getCredentials(HTTP_TRANSPORT))
              .setApplicationName(APPLICATION_NAME)
              .build();
      List<Sheet> sheets = service.spreadsheets().get(AuctionSheetId).execute().getSheets();
      String sheetName = createSheetNameByMonth(0);
      System.out.println("sheet name: " + sheetName);

      if (!checkSheetNameExist(sheetName, sheets)) {
        System.out.println("create new sheet");
        createNewSheet(sheetName, AuctionSheetId);
      }
      ValueRange sheetValue =
          service.spreadsheets().values().get(AuctionSheetId, sheetName).execute();

      List<List<Object>> data =
          formatAutionData(auction, bids, winner, auctionRegister, address, sheetValue);
      RangeObject range = findFirstRange("STT", sheetValue);
      insertDataToSheet(
          data, moveRange(range, 1, 0).getRange(), AuctionSheetId, service, sheetName);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("export aution ex: " + e.getMessage());
    }
  }

  public List<List<Object>> formatAutionData(
      Auction auction,
      int bids,
      User winner,
      AuctionRegister auctionRegister,
      Address address,
      ValueRange sheetValue) {
    List<Object> row = new ArrayList<>();
    RangeObject sttRange = findFirstRange("STT", sheetValue);
    int lastRowWriten = findLastWrittenRowOfColumn(sttRange, sttRange.getColumn(), sheetValue);
    row.add(lastRowWriten - 2);
    row.add(auction.getCreated().format(formatter));
    row.add(auction.getId());
    row.add(auction.getAssest().getId());
    row.add(auction.getAssest().getName());
    row.add(auction.getBuyPrice());
    row.add(auction.getBidPrice());
    int attendingUser = auctionRegisterRepository.findTurnByAuction_Id(auction.getId());
    // hide info
    row.add(bids);
    row.add(attendingUser);
    row.add(auction.getRegistrationFee().longValue());
    row.add(auction.getRegistrationFee().longValue() * attendingUser);

    row.add(auction.getWinPrice().longValue());
    row.add(auction.getWinner());
    row.add(winner.getName());
    row.add(winner.getEmail());
    row.add(address.getMobile());
    row.add(address.getAddress());
    row.add(auctionRegister.getWarranty().floatValue());
    List<List<Object>> values = Arrays.asList(row);
    return values;
  }

  public static ValueRange getSheetValue(String sheetId, String sheetName) throws Exception {
    try {
      NetHttpTransport HTTP_TRANSPORT = null;
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      Sheets service =
          new Sheets.Builder(
                  HTTP_TRANSPORT,
                  JacksonFactory.getDefaultInstance(),
                  getCredentials(HTTP_TRANSPORT))
              .setApplicationName(APPLICATION_NAME)
              .build();
      ValueRange sheetValue = service.spreadsheets().values().get(sheetId, sheetName).execute();
      return sheetValue;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static boolean checkSheetNameExist(String name, List<Sheet> sheets) throws IOException {
    boolean sheetExist = false;
    log.info("numberOfSheet: " + new Gson().toJson(sheets));
    int numberOfSheet = sheets.size();
    log.info("numberOfSheet: " + numberOfSheet);
    for (int i = 0; i < numberOfSheet; i++) {
      String sheetName = sheets.get(i).getProperties().getTitle();
      if (sheetName.equals(name)) {
        sheetExist = true;
      }
    }
    return sheetExist;
  }

  public static RangeObject moveRange(RangeObject range, int moveRow, int moveColumn) {
    RangeObject newRange = new RangeObject();
    newRange.setRow(range.getRow() + moveRow);
    newRange.setColumn(nextChar(range.getColumn(), moveColumn));
    newRange.setRange(newRange.getColumn() + newRange.getRow());
    return newRange;
  }

  //	insert data to sheet from this range
  public static void insertDataToSheet(
      List<List<Object>> data, String range, String spreadsheetId, Sheets service, String sheetName)
      throws IOException {
    log.info("start insert data");
    System.out.println("sheet name: " + sheetName);
    System.out.println(new Gson().toJson(data));

    ValueRange body = new ValueRange().setValues(data);
    //			UpdateValuesResponse result = service.spreadsheets().values().update(spreadsheetId, range,
    // body)
    //					.setValueInputOption("RAW").execute();
    Sheets.Spreadsheets.Values.Append request =
        service.spreadsheets().values().append(spreadsheetId, sheetName + "!" + range, body);
    request.setValueInputOption("USER_ENTERED");
    request.setInsertDataOption("INSERT_ROWS");
    AppendValuesResponse response = request.execute();
    System.out.printf(response.getUpdates().getUpdatedRows() + " rows have been update!");
  }

  public static void insertMultipDataToSheet(
      List<List<Object>> data, String range, String spreadsheetId, Sheets service, String sheetName)
      throws IOException {
    log.info("start insert data");
    System.out.println("sheet name: " + sheetName);
    List<ValueRange> values = new ArrayList<ValueRange>();
    values.add(new ValueRange().setRange(sheetName + "!" + range).setValues(data));
    // Additional ranges to update ...

    BatchUpdateValuesRequest body =
        new BatchUpdateValuesRequest().setValueInputOption("USER_ENTERED").setData(values);
    BatchUpdateValuesResponse result =
        service.spreadsheets().values().batchUpdate(spreadsheetId, body).execute();
    System.out.printf("%d cells updated.", result.getTotalUpdatedCells());
  }

  public static String nextChar(String startChar, int next) {
    int index = aToZ.indexOf(startChar);
    return aToZ.charAt(index + next) + "";
  }

  public static String getValueOfRange(String range, ValueRange sheetValue) {
    String column = range.charAt(0) + "";
    int row = Integer.parseInt(range.substring(1, range.length()));
    List<Object> thisRow = sheetValue.getValues().get(row - 1);
    log.info(thisRow.toString());
    return thisRow.get(aToZ.indexOf(column)).toString();
  }

  public static RangeObject findFirstRange(String input, ValueRange sheetValue) {
    int row = sheetValue.getValues().size();
    for (int i = 0; i < row; i++) {
      List<Object> thisRow = sheetValue.getValues().get(i);
      for (int j = 0; j < thisRow.size(); j++) {
        if (thisRow.get(j).toString().trim().equals(input.trim())) {
          RangeObject range =
              new RangeObject(aToZ.charAt(j + 1) + "" + (i + 1), i + 1, aToZ.charAt(j + 1) + "");
          return range;
        }
      }
    }
    return null;
  }

  public static int getColumnIndex(String column) {
    return aToZ.indexOf(column);
  }

  public static int findLastWrittenRowOfColumn(
      RangeObject startRange, String column, ValueRange sheetValue) {
    int row = sheetValue.getValues().size();
    for (int i = startRange.getRow(); i < row; i++) {
      List<Object> thisRow = sheetValue.getValues().get(i);
      if (thisRow.size() <= getColumnIndex(column) - 1) {
        return i;
      }
      if (thisRow.get(getColumnIndex(column) - 1) == null) {
        return i;
      }
    }
    return row;
  }

  //	find location of input in this row
  public static String findColumnRange(String input, int row, ValueRange sheetValue) {
    List<Object> thisRow = sheetValue.getValues().get(row);
    for (int j = 0; j < thisRow.size(); j++) {
      log.info("value: " + thisRow.get(j).toString());
      if (thisRow.get(j).toString().equals(input)) {
        return aToZ.charAt(j + 1) + "";
      }
    }
    return null;
  }

  //	find location of input in this column
  public static int findRowRange(String input, String column, ValueRange sheetValue) {
    int row = sheetValue.getValues().size();
    for (int i = 0; i < row; i++) {
      List<Object> thisRow = sheetValue.getValues().get(i);
      log.info("row: " + i + " row size: " + thisRow.size());
      for (int j = 0; j < thisRow.size(); j++) {
        log.info("value: " + thisRow.get(j).toString());
        if (aToZ.indexOf(column) == j && thisRow.get(j).toString().equals(input)) {
          return i + 1;
        }
      }
    }
    return -1;
  }

  // find last range of sheet
  public static RangeObject findLastRange(ValueRange sheetValue) throws IOException {
    if (sheetValue.getValues() == null) {
      return new RangeObject("A1", 1, "A");
    }
    int row = sheetValue.getValues().size();
    log.info("sheet size: " + row);
    String column = aToZ.charAt(sheetValue.getValues().get(row).size() + 1) + "";
    return new RangeObject(column + "" + row, row, column);
  }

  // check current day and create sheet name form: DD/MM/yyyy
  public static String createSheetNameByMonth(int clHour) {
    long now = System.currentTimeMillis();
    Calendar c = Calendar.getInstance();
    c.setTime(new Date(now + (7 + clHour) * 60 * 60 * 1000));
    int month = c.get(Calendar.MONTH) + 1;
    return "Tháng " + month;
  }

  // create new sheet
  public static void createNewSheet(String title, String spreadsheetId) throws Exception {
    log.info("title: " + title);
    NetHttpTransport HTTP_TRANSPORT = null;
    HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Sheets service =
        new Sheets.Builder(
                HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build();
    List<Request> requests = new ArrayList<>();
    requests.add(
        new Request()
            .setAddSheet(
                new AddSheetRequest()
                    .setProperties(new SheetProperties().setTitle(title).setIndex(0))));
    BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
    BatchUpdateSpreadsheetResponse response =
        service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
    log.info(response.toString());
  }
}
