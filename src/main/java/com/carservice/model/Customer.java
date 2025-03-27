package com.carservice.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
  private Long customerId;
  private String forename;
  private String surname;
  private String address;
  private String postCode;
  private String phoneNo;
  private List<Car> cars;

  // Default constructor
  public Customer() {
    this.cars = new ArrayList<>();
  }

  // Constructor with fields
  public Customer(
      Long customerId,
      String forename,
      String surname,
      String address,
      String postCode,
      String phoneNo) {
    this.customerId = customerId;
    this.forename = forename;
    this.surname = surname;
    this.address = address;
    this.postCode = postCode;
    this.phoneNo = phoneNo;
    this.cars = new ArrayList<>();
  }

  // Getters and Setters
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
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

  public List<Car> getCars() {
    return cars;
  }

  public void setCars(List<Car> cars) {
    this.cars = cars;
  }

  // Helper method to add a car
  public void addCar(Car car) {
    cars.add(car);
  }
}
