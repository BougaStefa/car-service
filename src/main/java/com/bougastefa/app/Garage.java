package com.bougastefa.app;

public class Garage {
  private String garageID;
  private String garageName;
  private String address;
  private String town;
  private String postCode;
  private String phoneNo;

  public Garage(String garageID, String garageName, String address, String town, String postCode, String phoneNo) {
    this.garageID = garageID;
    this.garageName = garageName;
    this.address = address;
    this.town = town;
    this.postCode = postCode;
    this.phoneNo = phoneNo;
  }

  public void setGarageID(String garageID) {
    this.garageID = garageID;
  }

  public void setGarageName(String garageName) {
    this.garageName = garageName;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setTown(String town) {
    this.town = town;
  }

  public void setPostCode(String postCode) {
    this.postCode = postCode;
  }

  public void setPhoneNo(String phoneNo) {
    this.phoneNo = phoneNo;
  }

  public String getGarageID() {
    return garageID;
  }

  public String getgn() {
    return garageName;
  }

  public String getaddress() {
    return address;
  }

  public String gettown() {
    return town;
  }

  public String getpostcode() {
    return postCode;
  }

  public String getphoneno() {
    return phoneNo;
  }

  public void addGarage() {
    // Add garage to database
  }

  public void editGarage() {
    // Edit garage in database
  }

  public void deleteGarage() {
    // Delete garage from database
  }

  public void searchGarage() {
    // Search garage in database
  }
}
