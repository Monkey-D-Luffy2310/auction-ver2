package vn.acgroup.service.googleSheet;

public class RangeObject {
  private String range;

  private int row;

  private String column;

  public String getRange() {
    return range;
  }

  public void setRange(String range) {
    this.range = range;
  }

  public int getRow() {
    return row;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  @Override
  public String toString() {
    return "RangeObject [range=" + range + ", row=" + row + ", column=" + column + "]";
  }

  public RangeObject() {}

  public RangeObject(String range, int row, String column) {
    this.range = range;
    this.row = row;
    this.column = column;
  }
}
