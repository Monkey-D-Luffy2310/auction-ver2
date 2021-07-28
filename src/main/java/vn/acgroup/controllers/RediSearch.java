package vn.acgroup.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.redisearch.client.Client;
import io.redisearch.Document;
import io.redisearch.SearchResult;
import io.redisearch.Query;
import io.redisearch.Schema;
import io.redisearch.client.IndexDefinition;
import redis.clients.jedis.Jedis;
import vn.acgroup.entities.Asset;
import vn.acgroup.entities.User;
import vn.acgroup.entities.Auction;
import vn.acgroup.repositories.AddressRepository;
import vn.acgroup.repositories.AssetRepository;
import vn.acgroup.repositories.AuctionRepository;
import vn.acgroup.repositories.BidRepository;
import vn.acgroup.repositories.UserRepository;

@Controller
@CrossOrigin(origins = "*")
public class RediSearch {

  public static Client client =
      new Client(
          "auction",
          "redis-19747.c238.us-central1-2.gce.cloud.redislabs.com",
          19747,
          50000,
          50000,
          "iuvr2bYMJJD4nC1rFKAPkmeskhHLAp2J");

  @Autowired AuctionRepository auctionRepository;
  @Autowired AssetRepository assetRepository;
  @Autowired UserRepository userRepository;
  @Autowired BidRepository bidRepository;
  @Autowired AddressRepository addressRepository;

  @GetMapping(value = "redisearch/getById/{docId}")
  @ResponseBody
  public Document getById(@PathVariable String docId)
      throws InterruptedException, ExecutionException {
    try {
      Document doc = client.getDocument(docId);
      return doc;
    } catch (Exception e) {
      return null;
    }
  }

  @DeleteMapping(value = "/redisearch/delete/{docId}")
  @ResponseBody
  public boolean delete(@PathVariable String docId)
      throws InterruptedException, ExecutionException {
    try {
      boolean del = client.deleteDocument(docId);
      return del;
    } catch (Exception e) {
      return false;
    }
  }

  @DeleteMapping(value = "/redisearch/deleteIndex/")
  @ResponseBody
  public ResponseEntity<String> deleteIndex() throws InterruptedException, ExecutionException {
    try {
      client.dropIndex();
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping(value = "/redisearch/createIndex/")
  @ResponseBody
  public ResponseEntity<String> createIndex() throws InterruptedException, ExecutionException {
    Schema sc =
        new Schema()
            .addNumericField("Id")
            .addTextField("Title", 10.0)
            .addNumericField("price")
            .addNumericField("bidPrice")
            .addTextField("Seller", 8.0)
            .addTextField("Mail", 8.0)
            .addTextField("AuctionStatus", 6.0)
            .addTextField("Category", 7.0)
            .addTextField("timeEnd", 1.0)
            .addTextField("timeStart", 1.0)
            .addTextField("Images", 1.0)
            .addTextField("AreaAsset", 5.0)
            .addTextField("AreaAuction", 5.0)
            .addTextField("TradeMark", 3.0)
            .addTextField("Color", 3.0)
            .addTextField("Origin", 3.0)
            .addTextField("HomeNetwork", 3.0)
            .addTextField("SimNumber", 3.0)
            .addTextField("LiveStream", 1.0);

    IndexDefinition def = new IndexDefinition();
    try {
      client.createIndex(sc, Client.IndexOptions.defaultOptions().setDefinition(def));
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  public static ResponseEntity<String> getData(
      Auction auction, User user, Asset asset, UserRepository _userRepository, Client client) {
    try {

      Map<String, String> fields = new HashMap<String, String>();

      fields.put("Id", auction.getId() + "");
      fields.put("Title", asset.getName().toLowerCase());
      fields.put("price", auction.getCurrentPrice() + "");
      fields.put("bidPrice", auction.getBidPrice() + "");
      fields.put(
          "Seller", _userRepository.findById(auction.getSeller()).get().getName().toLowerCase());
      fields.put(
          "Mail", _userRepository.findById(auction.getSeller()).get().getEmail().toLowerCase());
      fields.put("AuctionStatus", auction.getStatus().toLowerCase());
      fields.put("Category", auction.getCategory());
      fields.put("timeEnd", auction.getEndAt().toString());
      fields.put("timeStart", auction.getStartAt().toString());
      fields.put("Images", asset.getImages());
      fields.put("AreaAsset", asset.getArea().toLowerCase());
      fields.put("AreaAuction", auction.getArea().toLowerCase());
      fields.put("TradeMark", asset.getTrademark().toLowerCase());
      fields.put("Color", asset.getColor().toLowerCase());
      fields.put("Origin", asset.getOrigin().toLowerCase());
      fields.put("HomeNetwork", asset.getHomeNetwork());
      fields.put("SimNumber", asset.getSimNumber());
      fields.put("LiveStream", auction.getLiveStream() + "");
      Jedis connection = client.connection();
      connection.hset(auction.getId() + "", fields);
      //      if (auction.getStatus().equals("Active") || auction.getStatus().equals("Upcoming")) {
      //        if (client.getDocument(auction.getId() + "") == null) {
      //          client.addDocument(auction.getId() + "", fields);
      //        } else {
      //          client.updateDocument(auction.getId() + "", 1.0, fields);
      //        }
      //      }
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping(value = "/redisearch/insert/")
  @ResponseBody
  public ResponseEntity<String> insert() throws InterruptedException, ExecutionException {
    try {
      ArrayList<String> status = new ArrayList<>();
      status.add("Active");
      status.add("Upcoming");
      Iterable<Auction> auction = auctionRepository.findByStatusIn(status);
      auction.forEach(
          _auction -> {
            User user = userRepository.findUserByAuctions_Id(_auction.getId());
            Asset asset = assetRepository.findAssetByAuctions_Id(_auction.getId());
            getData(_auction, user, asset, userRepository, client);
          });
      return new ResponseEntity<>("OK", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Server Error: " + e.getMessage(), HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping(value = "/redisearch/getAll")
  @ResponseBody
  public SearchResult getAll() throws InterruptedException, ExecutionException {
    Query query = new Query("*");
    SearchResult result = client.search(query);
    return result;
  }

  public static StringBuilder titleFormat(String title) {
    StringBuilder result = new StringBuilder(title.length());
    String words[] = title.split("\\ ");
    for (int i = 0; i < words.length; i++) {
      if (words[i].matches("[^0-9]+"))
        result
            .append(Character.toUpperCase(words[i].charAt(0)))
            .append(words[i].substring(1))
            .append(" ");
      else {
        result.append(words[i].toUpperCase()).append(" ");
      }
    }
    return result;
  }

  @GetMapping(value = "/redisearch/mainSearch")
  @ResponseBody
  public Map<String, Object> search(String queryString)
      throws InterruptedException, ExecutionException {

    Map<String, Object> returnValue = new HashMap<>();
    Map<String, Object> resultMeta = new HashMap<>();

    Query query = new Query(queryString).setWithScores();

    SearchResult queryResult = client.search(query);
    resultMeta.put("queryString", queryString);
    resultMeta.put("totalResults", queryResult.totalResults);

    List<Map<String, Object>> docsToReturn = new ArrayList<>();
    List<Document> docs = queryResult.docs;

    for (Document doc : docs) {

      Map<String, Object> props = new HashMap<>();
      Map<String, Object> meta = new HashMap<>();
      meta.put("id", doc.getId());
      meta.put("score", doc.getScore());
      doc.getProperties()
          .forEach(
              e -> {
                props.put(e.getKey(), e.getValue());
              });

      Map<String, Object> docMeta = new HashMap<>();
      docMeta.put("meta", meta);
      docMeta.put("fields", props);
      docsToReturn.add(docMeta);
    }

    returnValue.put("docs", docsToReturn);

    return returnValue;
  }
}
