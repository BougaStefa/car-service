package com.bougastefa.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Job {
  private String jobID;
  private String carRegNo;
  private String garageID;
  private String dateIn;
  private String dateOut;
  private double cost;

  private static final String url = "jdbc:mysql://localhost:3306/car-service";
  private static final String user = "root";
  private static final String pw = "9917";

  public Job(String jobID, String carRegNo, String garageID, String dateIn, String dateOut, double cost) {
    this.jobID = jobID;
    this.carRegNo = carRegNo;
    this.garageID = garageID;
    this.dateIn = dateIn;
    this.dateOut = dateOut;
    this.cost = cost;
  }

  public void setJobID(String jobID) {
    this.jobID = jobID;
  }

  public void setCarRegNo(String carRegNo) {
    this.carRegNo = carRegNo;
  }

  public void setGarageID(String garageID) {
    this.garageID = garageID;
  }

  public void setDateIn(String dateIn) {
    this.dateIn = dateIn;
  }

  public void setDateOut(String dateOut) {
    this.dateOut = dateOut;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }

  public String getJobID() {
    return jobID;
  }

  public String getCarRegNo() {
    return carRegNo;
  }

  public String getGarageID() {
    return garageID;
  }

  public String getDateIn() {
    return dateIn;
  }

  public String getDateOut() {
    return dateOut;
  }

  public double getCost() {
    return cost;
  }

  private Connection connect() throws SQLException {
    return DriverManager.getConnection(url, user, pw);
  }

  public void addJob() {
    String sql = "INSERT INTO jobs (jobID, carRegNo, garageID, dateIn, dateOut, cost) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = connect(); PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, jobID);
      prst.setString(2, carRegNo);
      prst.setString(3, garageID);
      prst.setString(4, dateIn);
      prst.setString(5, dateOut);
      prst.setDouble(6, cost);
      prst.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void editJob() {
    String sql = "UPDATE jobs SET carRegNo = ?, garageID = ?, dateIn = ?, dateOut = ?, cost = ? WHERE jobID = ?";
    try (Connection conn = connect(); PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, carRegNo);
      prst.setString(2, garageID);
      prst.setString(3, dateIn);
      prst.setString(4, dateOut);
      prst.setDouble(5, cost);
      prst.setString(6, jobID);
      prst.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void deleteJob() {
    String sql = "DELETE FROM jobs WHERE jobID = ?";
    try (Connection conn = connect(); PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, jobID);
      prst.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void searchJob() {
    String sql = "SELECT * FROM jobs WHERE jobID = ?";
    try (Connection conn = connect(); PreparedStatement prst = conn.prepareStatement(sql)) {
      prst.setString(1, jobID);
      ResultSet rs = prst.executeQuery();
      while (rs.next()) {
        this.carRegNo = rs.getString("carRegNo");
        this.garageID = rs.getString("garageID");
        this.dateIn = rs.getString("dateIn");
        this.dateOut = rs.getString("dateOut");
        this.cost = rs.getDouble("cost");
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
