package com.bougastefa.app;

public class Customer {
  private String customerID;
  private String forename;
  private String surname;
  private String address;
  private String postCode;
  private String phoneNo;

  public Customer(String customerID, String forename, String surname, String address, String postCode, String phoneNo) {
    this.customerID = customerID;
    this.forename = forename;
    this.surname = surname;
    this.address = address;
    this.postCode = postCode;
    this.phoneNo = phoneNo;
  }

  public void setCustomerID(String customerID) {
    this.customerID = customerID;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setPostCode(String postCode) {
    this.postCode = postCode;
  }

  public void setPhoneNo(String phoneNo) {
    this.phoneNo = phoneNo;
  }

  public String getCustomerID() {
    return customerID;
  }

  public String getForename() {
    return forename;
  }

  public String getSurname() {
    return surname;
  }

  public String getAddress() {
    return address;
  }

  public String getPostCode() {
    return postCode;
  }

  public String getPhoneNo() {
    return phoneNo;
  }

  private void addCustomer() {
    // add customer to database
  }

  private void editCustomer() {
    // edit customer in database
  }

  private void deleteCustomer() {
    // delete customer from database
  }

  private void searchCustomer() {
    // search for customer in database
  }

}
