package vn.acgroup.controllers.api;

import java.math.BigDecimal;

public class AddAssetRequest {
  private String name;
  private BigDecimal initPrice;
  private String images;
  private String category;
  private String description;
  private String currentStatus;
  private Integer amount;
  private String area = "";

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

  private Integer manufactureYear = 0; // năm sản xuất

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
  private String homeNetwork = "";
  private String simNumber = "";

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

  public String getImages() {
    return images;
  }

  public void setImages(String images) {
    this.images = images;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCurrentStatus() {
    return currentStatus;
  }

  public void setCurrentStatus(String currentStatus) {
    this.currentStatus = currentStatus;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public Integer getFloorsNumber() {
    return floorsNumber;
  }

  public void setFloorsNumber(Integer floorsNumber) {
    this.floorsNumber = floorsNumber;
  }

  public long getAcreage() {
    return acreage;
  }

  public void setAcreage(long acreage) {
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

  public float getFacade() {
    return facade;
  }

  public void setFacade(float facade) {
    this.facade = facade;
  }

  public float getWayIn() {
    return wayIn;
  }

  public void setWayIn(float wayIn) {
    this.wayIn = wayIn;
  }

  public double getPricePerSquareMetre() {
    return pricePerSquareMetre;
  }

  public void setPricePerSquareMetre(double pricePerSquareMetre) {
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

  public float getWeight() {
    return weight;
  }

  public void setWeight(float weight) {
    this.weight = weight;
  }

  public float getBrightness() {
    return brightness;
  }

  public void setBrightness(float brightness) {
    this.brightness = brightness;
  }

  public String getShape() {
    return shape;
  }

  public void setShape(String shape) {
    this.shape = shape;
  }

  public float getHeight() {
    return height;
  }

  public void setHeight(float height) {
    this.height = height;
  }

  public float getStumpDiameter() {
    return stumpDiameter;
  }

  public void setStumpDiameter(float stumpDiameter) {
    this.stumpDiameter = stumpDiameter;
  }

  public String getHomeNetwork() {
    return homeNetwork;
  }

  public void setHomeNetwork(String homeNetwork) {
    this.homeNetwork = homeNetwork;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSimNumber() {
    return simNumber;
  }

  public void setSimNumber(String simNumber) {
    this.simNumber = simNumber;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }
}
