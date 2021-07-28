package vn.acgroup.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import vn.acgroup.controllers.api.AddAssetRequest;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Asset {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asset_generator")
  @SequenceGenerator(name = "asset_generator", sequenceName = "asset_seq", allocationSize = 500)
  private long id;

  private LocalDateTime created = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

  private LocalDateTime updated = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
  private String name;

  private BigDecimal initPrice;
  private BigDecimal currentPrice;
  private BigDecimal finalPrice = BigDecimal.ZERO;
  private String images;
  private String status = "waiting";
  private Integer amount = 0;
  private String area = "";

  private String currentStatus = "";

  // bất động sản
  private Integer floorsNumber = 0;
  private long acreage = 0; // diện tích
  private String direction = ""; // hướng nhà
  private Integer bedroomsNumber = 0;
  private Integer toiletsNumber = 0;

  // đất
  private float facade = 0F; // mặt tiền
  private float wayIn = 0F; // đường vào
  private double pricePerSquareMetre = 0D; // giá/ 1m^2

  // xe hơi
  private Integer seatsNumber = 0; // số chỗ ngồi
  private String gear = ""; // hộp số
  private String fuel = ""; // nhiên liệu
  private String color = "";
  private String interiorColor = ""; // màu nội thất
  private String origin = ""; // xuất xứ

  private Integer manufactureYear = 0;

  private String trademark = ""; // thương hiệu
  private Integer consume = 0; // tiêu thụ lít/100km

  // xe máy
  private Integer cubic = 0; // phân khối 50,125,150cc
  private String type = ""; // loại xe : xe số, xe ga

  // đồ cổ
  private String antiques = ""; // loại đồ cổ : bình cổ, xe cổ, tượng cổ
  private Integer yearOld = 0; // tuổi đời

  // đá quý
  private float weight = 0F;
  private float brightness = 0F; // độ sáng 4 - 4.5 - 5
  private String shape = ""; // hình dạng tròn vuông

  // cây cảnh
  private float height = 0F;
  private float stumpDiameter = 0F; // đường kính gốc : 1,5m

  // sim
  private String homeNetwork = ""; // nhà mạng
  private String simNumber = ""; // số sim

  // điện thoại
  private String resolution = ""; // độ phân giải (pixels)
  private float screenSize = 0F; // kích thước màn hình (inch)
  private String camera = "";
  private String operatingSystem = ""; // Hệ điều hành
  private String cpu = "";
  private int ram = 0; // (GB)
  private int rom = 0; // (GB)
  private String sim = "";
  private int batteryCapacity = 0; // dung lượng pin (mAh)

  // gia dụng
  private int wattage = 0; // công suất (W)
  private int guarantee = 0; // bảo hành (tháng)

  public int getWattage() {
    return wattage;
  }

  public void setWattage(int wattage) {
    this.wattage = wattage;
  }

  public int getGuarantee() {
    return guarantee;
  }

  public void setGuarantee(int guarantee) {
    this.guarantee = guarantee;
  }

  public String getResolution() {
    return resolution;
  }

  public void setResolution(String resolution) {
    this.resolution = resolution;
  }

  public float getScreenSize() {
    return screenSize;
  }

  public void setScreenSize(float screenSize) {
    this.screenSize = screenSize;
  }

  public String getCamera() {
    return camera;
  }

  public void setCamera(String camera) {
    this.camera = camera;
  }

  public String getOperatingSystem() {
    return operatingSystem;
  }

  public void setOperatingSystem(String operatingSystem) {
    this.operatingSystem = operatingSystem;
  }

  public String getCpu() {
    return cpu;
  }

  public void setCpu(String cpu) {
    this.cpu = cpu;
  }

  public int getRam() {
    return ram;
  }

  public void setRam(int ram) {
    this.ram = ram;
  }

  public int getRom() {
    return rom;
  }

  public void setRom(int rom) {
    this.rom = rom;
  }

  public String getSim() {
    return sim;
  }

  public void setSim(String sim) {
    this.sim = sim;
  }

  public int getBatteryCapacity() {
    return batteryCapacity;
  }

  public void setBatteryCapacity(int batteryCapacity) {
    this.batteryCapacity = batteryCapacity;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getSimNumber() {
    return simNumber;
  }

  public void setSimNumber(String simNumber) {
    this.simNumber = simNumber;
  }

  public String getHomeNetwork() {
    return homeNetwork;
  }

  public void setHomeNetwork(String homeNetwork) {
    this.homeNetwork = homeNetwork;
  }

  public String getDescription() {
    return description;
  }

  public String getCurrentStatus() {
    return currentStatus;
  }

  public void setCurrentStatus(String currentStatus) {
    this.currentStatus = currentStatus;
  }

  public void setAcreage(long acreage) {
    this.acreage = acreage;
  }

  public void setFacade(float facade) {
    this.facade = facade;
  }

  public void setWayIn(float wayIn) {
    this.wayIn = wayIn;
  }

  public void setPricePerSquareMetre(double pricePerSquareMetre) {
    this.pricePerSquareMetre = pricePerSquareMetre;
  }

  public void setWeight(float weight) {
    this.weight = weight;
  }

  public void setBrightness(float brightness) {
    this.brightness = brightness;
  }

  public void setHeight(float height) {
    this.height = height;
  }

  public void setStumpDiameter(float stumpDiameter) {
    this.stumpDiameter = stumpDiameter;
  }

  public Integer getFloorsNumber() {
    return floorsNumber;
  }

  public void setFloorsNumber(Integer floorsNumber) {
    this.floorsNumber = floorsNumber;
  }

  public Long getAcreage() {
    return acreage;
  }

  public void setAcreage(Long acreage) {
    this.acreage = acreage;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public Integer getBedroomsNumber() {
    return bedroomsNumber;
  }

  public void setBedroomsNumber(Integer bedroomsNumber) {
    this.bedroomsNumber = bedroomsNumber;
  }

  public Integer getToiletsNumber() {
    return toiletsNumber;
  }

  public void setToiletsNumber(Integer toiletsNumber) {
    this.toiletsNumber = toiletsNumber;
  }

  public Float getFacade() {
    return facade;
  }

  public void setFacade(Float facade) {
    this.facade = facade;
  }

  public Float getWayIn() {
    return wayIn;
  }

  public void setWayIn(Float wayIn) {
    this.wayIn = wayIn;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public Double getPricePerSquareMetre() {
    return pricePerSquareMetre;
  }

  public void setPricePerSquareMetre(Double pricePerSquareMetre) {
    this.pricePerSquareMetre = pricePerSquareMetre;
  }

  public Integer getSeatsNumber() {
    return seatsNumber;
  }

  public void setSeatsNumber(Integer seatsNumber) {
    this.seatsNumber = seatsNumber;
  }

  public String getGear() {
    return gear;
  }

  public void setGear(String gear) {
    this.gear = gear;
  }

  public String getFuel() {
    return fuel;
  }

  public void setFuel(String fuel) {
    this.fuel = fuel;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getInteriorColor() {
    return interiorColor;
  }

  public void setInteriorColor(String interiorColor) {
    this.interiorColor = interiorColor;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public Integer getManufactureYear() {
    return manufactureYear;
  }

  public void setManufactureYear(Integer manufactureYear) {
    this.manufactureYear = manufactureYear;
  }

  public String getTrademark() {
    return trademark;
  }

  public void setTrademark(String trademark) {
    this.trademark = trademark;
  }

  public Integer getConsume() {
    return consume;
  }

  public void setConsume(Integer consume) {
    this.consume = consume;
  }

  public Integer getCubic() {
    return cubic;
  }

  public void setCubic(Integer cubic) {
    this.cubic = cubic;
  }

  public String getAntiques() {
    return antiques;
  }

  public void setAntiques(String antiques) {
    this.antiques = antiques;
  }

  public Integer getYearOld() {
    return yearOld;
  }

  public void setYearOld(Integer yearOld) {
    this.yearOld = yearOld;
  }

  public Float getWeight() {
    return weight;
  }

  public void setWeight(Float weight) {
    this.weight = weight;
  }

  public Float getBrightness() {
    return brightness;
  }

  public void setBrightness(Float brightness) {
    this.brightness = brightness;
  }

  public String getShape() {
    return shape;
  }

  public void setShape(String shape) {
    this.shape = shape;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Float getHeight() {
    return height;
  }

  public void setHeight(Float height) {
    this.height = height;
  }

  public Float getStumpDiameter() {
    return stumpDiameter;
  }

  public void setStumpDiameter(Float stumpDiameter) {
    this.stumpDiameter = stumpDiameter;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  private String category;
  private String description;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id")
  @JsonBackReference
  private User user;

  private ArrayList<String> tags = new ArrayList<String>();

  @OneToMany(mappedBy = "assest", fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Auction> auctions;

  public List<Auction> getAuctions() {
    return auctions;
  }

  public void setAuctions(List<Auction> auctions) {
    this.auctions = auctions;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getInitPrice() {
    return initPrice;
  }

  public void setInitPrice(BigDecimal initPrice) {
    this.initPrice = initPrice;
  }

  public BigDecimal getCurrentPrice() {
    return currentPrice;
  }

  public void setCurrentPrice(BigDecimal currentPrice) {
    this.currentPrice = currentPrice;
  }

  public BigDecimal getFinalPrice() {
    return finalPrice;
  }

  public void setFinalPrice(BigDecimal finalPrice) {
    this.finalPrice = finalPrice;
  }

  public String getImages() {
    return images;
  }

  public void setImages(String images) {
    this.images = images;
  }

  public ArrayList<String> getTags() {
    return tags;
  }

  public void setTags(ArrayList<String> tags) {
    this.tags = tags;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public LocalDateTime getUpdated() {
    return updated;
  }

  public void setUpdated(LocalDateTime updated) {
    this.updated = updated;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Asset() {
    super();
  }

  public void mapAsset(AddAssetRequest addAssetRequest) {
    this.setName(addAssetRequest.getName());
    this.setInitPrice(addAssetRequest.getInitPrice());
    //    this.setCurrentPrice(addAssetRequest.getInitPrice());
    this.setImages(addAssetRequest.getImages());
    this.setCategory(addAssetRequest.getCategory());
    this.setDescription(addAssetRequest.getDescription());
    this.setCurrentStatus(addAssetRequest.getCurrentStatus());
    this.setAmount(addAssetRequest.getAmount());
    this.setArea(addAssetRequest.getArea());

    this.setFloorsNumber(addAssetRequest.getFloorsNumber());
    this.setAcreage(addAssetRequest.getAcreage());
    this.setDirection(addAssetRequest.getDirection());
    this.setBedroomsNumber(addAssetRequest.getBedroomsNumber());
    this.setToiletsNumber(addAssetRequest.getToiletsNumber());

    this.setFacade(addAssetRequest.getFacade());
    this.setWayIn(addAssetRequest.getWayIn());
    this.setPricePerSquareMetre(addAssetRequest.getPricePerSquareMetre());

    this.setSeatsNumber(addAssetRequest.getSeatsNumber());
    this.setGear(addAssetRequest.getGear());
    this.setFuel(addAssetRequest.getFuel());
    this.setColor(addAssetRequest.getColor());
    this.setInteriorColor(addAssetRequest.getInteriorColor());
    this.setOrigin(addAssetRequest.getOrigin());
    this.setManufactureYear(addAssetRequest.getManufactureYear());
    this.setTrademark(addAssetRequest.getTrademark());
    this.setConsume(addAssetRequest.getConsume());

    this.setCubic(addAssetRequest.getCubic());
    this.setType(addAssetRequest.getType());

    this.setAntiques(addAssetRequest.getAntiques());
    this.setYearOld(addAssetRequest.getYearOld());

    this.setWeight(addAssetRequest.getWayIn());
    this.setBrightness(addAssetRequest.getBrightness());
    this.setShape(addAssetRequest.getShape());

    this.setHeight(addAssetRequest.getHeight());
    this.setStumpDiameter(addAssetRequest.getStumpDiameter());

    this.setHomeNetwork(addAssetRequest.getHomeNetwork());
    this.setSimNumber(addAssetRequest.getSimNumber());

    this.setResolution(addAssetRequest.getResolution());
    this.setScreenSize(addAssetRequest.getScreenSize());
    this.setCamera(addAssetRequest.getCamera());
    this.setOperatingSystem(addAssetRequest.getOperatingSystem());
    this.setCpu(addAssetRequest.getCpu());
    this.setRam(addAssetRequest.getRam());
    this.setRom(addAssetRequest.getRom());
    this.setSim(addAssetRequest.getSim());
    this.setBatteryCapacity(addAssetRequest.getBatteryCapacity());

    this.setWattage(addAssetRequest.getWattage());
    this.setGuarantee(addAssetRequest.getGuarantee());
  }
}
