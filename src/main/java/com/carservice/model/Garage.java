package com.carservice.model;

public class Garage {
  private Long garageId;
  private String garageName;
  private String address;
  private String town;
  private String postCode;
  private String phoneNo;

  // Default constructor
  public Garage() {}

  // Constructor with fields
  public Garage(
      Long garageId,
      String garageName,
      String address,
      String town,
      String postCode,
      String phoneNo) {
    this.garageId = garageId;
    this.garageName = garageName;
    this.address = address;
    this.town = town;
    this.postCode = postCode;
    this.phoneNo = phoneNo;
  }

  // Getters and Setters
  public Long getGarageId() {
    return garageId;
  }

  public void setGarageId(Long garageId) {
    this.garageId = garageId;
  }

  public String getGarageName() {
    return garageName;
  }

  public void setGarageName(String garageName) {
    this.garageName = garageName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;
  }

  public String getPostCode() {
    return postCode;
  }

  public void setPostCode(String postCode) {
    this.postCode = postCode;
  }

  public String getPhoneNo() {
    return phoneNo;
  }

  public void setPhoneNo(String phoneNo) {
    this.phoneNo = phoneNo;
  }
}
