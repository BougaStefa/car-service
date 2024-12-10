package com.bougastefa.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Car {
  private String regNo;
  private String make;
  private String model;
  private String year;
  private String customerID;

  private static final String url = "jdbc:mysql://localhost:3306/car-service";
  private static final String user = "user";
  private static final String pw = "9917";

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

  private Connection connect() throws SQLException {
    return DriverManager.getConnection(url, user, pw);
  }

  public void addCar() {
    String sql = "INSERT INTO cars (regNo, make, model, year, customerID) VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = connect(); PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, regNo);
      prst.setString(2, make);
      prst.setString(3, model);
      prst.setString(4, year);
      prst.setString(5, customerID);
      prst.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void editCar() {
    String sql = "UPDATE cars SET make = ?, model = ?, year = ?, customerID = ? WHERE regNo = ?";
    try (Connection conn = connect(); PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, make);
      prst.setString(2, model);
      prst.setString(3, year);
      prst.setString(4, customerID);
      prst.setString(5, regNo);
      prst.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void deleteCar() {
    String sql = "DELETE FROM cars WHERE regNo = ?";
    try (Connection conn = connect(); PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, regNo);
      prst.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void searchCar() {
    String sql = "SELECT * FROM cars WHERE regNo = ?";
    try (Connection conn = connect(); PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, regNo);
      ResultSet rs = prst.executeQuery();
      while (rs.next()) {
        this.make = rs.getString("make");
        this.model = rs.getString("model");
        this.year = rs.getString("year");
        this.customerID = rs.getString("customerID");
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
