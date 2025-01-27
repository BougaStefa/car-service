package com.bougastefa.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Garage {
  private String garageID;
  private String garageName;
  private String address;
  private String town;
  private String postCode;
  private String phoneNo;

  private static final String url = "jdbc:mysql://localhost:3306/car-service";
  private static final String user = "user";
  private static final String pw = "9917";

  public Garage(
      String garageID,
      String garageName,
      String address,
      String town,
      String postCode,
      String phoneNo) {
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

  public String getGarageName() {
    return garageName;
  }

  public String getAddress() {
    return address;
  }

  public String getTown() {
    return town;
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

  public void addGarage() {
    String sql =
        "INSERT INTO garages (garageID, garageName, address, town, postCode, phoneNo) VALUES (?, ?,"
            + " ?, ?, ?, ?)";
    try (Connection conn = connect();
        PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, garageID);
      prst.setString(2, garageName);
      prst.setString(3, address);
      prst.setString(4, town);
      prst.setString(5, postCode);
      prst.setString(6, phoneNo);
      prst.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void editGarage() {
    String sql =
        "UPDATE garages SET garageName = ?, address = ?, town = ?, postCode = ?, phoneNo = ? WHERE"
            + " garageID = ?";
    try (Connection conn = connect();
        PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, garageName);
      prst.setString(2, address);
      prst.setString(3, town);
      prst.setString(4, postCode);
      prst.setString(5, phoneNo);
      prst.setString(6, garageID);
      prst.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void deleteGarage() {
    String sql = "DELETE FROM garages WHERE garageID = ?";
    try (Connection conn = connect();
        PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, garageID);
      prst.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void searchGarage() {
    String sql = "SELECT * FROM garages WHERE garageID = ?";
    try (Connection conn = connect();
        PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, garageID);
      ResultSet rs = prst.executeQuery();
      while (rs.next()) {
        this.garageName = rs.getString("garageName");
        this.address = rs.getString("address");
        this.town = rs.getString("town");
        this.postCode = rs.getString("postCode");
        this.phoneNo = rs.getString("phoneNo");
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
