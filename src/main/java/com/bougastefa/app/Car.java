package com.bougastefa.app;

public class Car {
  private String regNo;
  private String make;
  private String model;
  private String year;
  private String customerID;

  public Car(String regNo, String make, String model, String year, String customerID) {
    this.regNo = regNo;
    this.make = make;
    this.model = model;
    this.year = year;
    this.customerID = customerID;
  }

  public void setRegNo(String regNo) {
    this.regNo = regNo;
  }

  public void setMake(String make) {
    this.make = make;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public void setCustomerID(String customerID) {
    this.customerID = customerID;
  }

  public String getRegNo() {
    return regNo;
  }

  public String getMake() {
    return make;
  }

  public String getModel() {
    return model;
  }

  public String getYear() {
    return year;
  }

  public String getCustomerID() {
    return customerID;
  }

  public void addCar() {
    // add car to database
  }

  public void editCar() {
    // edit car in database
  }

  public void deleteCar() {
    // delete car from database
  }

  public void searchCar() {
    // search for car in database
  }

}
