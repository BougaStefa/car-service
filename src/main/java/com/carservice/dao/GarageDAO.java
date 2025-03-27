package com.carservice.dao;

import com.carservice.config.DatabaseConfig;
import com.carservice.model.Garage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GarageDAO implements CrudDAO<Garage, Long> {
  private static final String FIND_BY_ID = "SELECT * FROM Garage WHERE garageId = ?";
  private static final String FIND_ALL = "SELECT * FROM Garage";
  private static final String INSERT =
      "INSERT INTO Garage (garageName, address, town, postCode, phoneNo) VALUES (?, ?, ?, ?, ?)";
  private static final String UPDATE =
      "UPDATE Garage SET garageName = ?, address = ?, town = ?, postCode = ?, phoneNo = ? WHERE"
          + " garageId = ?";
  private static final String DELETE = "DELETE FROM Garage WHERE garageId = ?";

  @Override
  public Garage findById(Long id) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {
      stmt.setLong(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapRowToGarage(rs);
        }
      }
    }
    return null;
  }

  @Override
  public List<Garage> findAll() throws SQLException {
    List<Garage> garages = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(FIND_ALL)) {
      while (rs.next()) {
        garages.add(mapRowToGarage(rs));
      }
    }
    return garages;
  }

  @Override
  public Long save(Garage garage) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
      setGarageParameters(stmt, garage);
      stmt.executeUpdate();
      try (ResultSet rs = stmt.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getLong(1);
        }
      }
    }
    return null;
  }

  @Override
  public boolean update(Garage garage) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
      setGarageParameters(stmt, garage);
      stmt.setLong(6, garage.getGarageId());
      return stmt.executeUpdate() > 0;
    }
  }

  @Override
  public boolean delete(Long id) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(DELETE)) {
      stmt.setLong(1, id);
      return stmt.executeUpdate() > 0;
    }
  }

  private Garage mapRowToGarage(ResultSet rs) throws SQLException {
    return new Garage(
        rs.getLong("garageId"),
        rs.getString("garageName"),
        rs.getString("address"),
        rs.getString("town"),
        rs.getString("postCode"),
        rs.getString("phoneNo"));
  }

  private void setGarageParameters(PreparedStatement stmt, Garage garage) throws SQLException {
    stmt.setString(1, garage.getGarageName());
    stmt.setString(2, garage.getAddress());
    stmt.setString(3, garage.getTown());
    stmt.setString(4, garage.getPostCode());
    stmt.setString(5, garage.getPhoneNo());
  }
}
