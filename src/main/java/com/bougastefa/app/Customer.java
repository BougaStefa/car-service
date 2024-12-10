package com.bougastefa.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Customer {
  private final String url = "jdbc:mysql://localhost:3306/car-service";
  private final String user = "root";
  private final String pw = "9917";
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

  private Connection connect() throws SQLException {
    return DriverManager.getConnection(url, user, pw);
  }

  private void addCustomer() {
    String sql = "IINSERT INTO customers (customerID, forename, surname, address, postCode, phoneNo) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = connect(); PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, customerID);
      prst.setString(2, forename);
      prst.setString(3, surname);
      prst.setString(4, address);
      prst.setString(5, postCode);
      prst.setString(6, phoneNo);
      prst.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Could not connect to the database: " + e.getMessage());
    }
  }

  private void editCustomer() {
    String sql = "UPDATE customers SET forename = ?, surname = ?, address = ?, postCode = ?, phoneNo = ? WHERE customerID = ?";
    try (Connection conn = connect(); PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, forename);
      prst.setString(2, surname);
      prst.setString(3, address);
      prst.setString(4, postCode);
      prst.setString(5, phoneNo);
      prst.setString(6, customerID);
      prst.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Could not connect to the database: " + e.getMessage());
    }
  }

  private void deleteCustomer() {
    String sql = "DELETE FROM customers WHERE customerID = ?";
    try (Connection conn = connect(); PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, customerID);
      prst.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  private void searchCustomer() {
    String sql = "SELECT * FROM customers WHERE customerID = ?";
    try (Connection conn = connect(); PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, customerID);
      ResultSet rs = prst.executeQuery();
      while (rs.next()) {
        this.forename = rs.getString("forename");
        this.surname = rs.getString("surname");
        this.address = rs.getString("address");
        this.postCode = rs.getString("postCode");
        this.phoneNo = rs.getString("phoneNo");
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

}
