package com.carservice.dao;

import com.carservice.config.DatabaseConfig;
import com.carservice.model.Car;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDAO implements CrudDAO<Car, String> {
  private static final String FIND_BY_ID = "SELECT * FROM Car WHERE regNo = ?";
  private static final String FIND_ALL = "SELECT * FROM Car";
  private static final String INSERT =
      "INSERT INTO Car (regNo, make, model, year, customerId) VALUES (?, ?, ?, ?, ?)";
  private static final String UPDATE =
      "UPDATE Car SET make = ?, model = ?, year = ?, customerId = ? WHERE regNo = ?";
  private static final String DELETE = "DELETE FROM Car WHERE regNo = ?";
  private static final String FIND_BY_CUSTOMER = "SELECT * FROM Car WHERE customerId = ?";

  @Override
  public Car findById(String regNo) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {
      stmt.setString(1, regNo);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapRowToCar(rs);
        }
      }
    }
    return null;
  }

  @Override
  public List<Car> findAll() throws SQLException {
    List<Car> cars = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(FIND_ALL)) {
      while (rs.next()) {
        cars.add(mapRowToCar(rs));
      }
    }
    return cars;
  }

  public List<Car> findByCustomer(Long customerId) throws SQLException {
    List<Car> cars = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_CUSTOMER)) {
      stmt.setLong(1, customerId);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          cars.add(mapRowToCar(rs));
        }
      }
    }
    return cars;
  }

  @Override
  public String save(Car car) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(INSERT)) {
      setCarParameters(stmt, car);
      stmt.executeUpdate();
      return car.getRegNo();
    }
  }

  @Override
  public boolean update(Car car) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
      stmt.setString(1, car.getMake());
      stmt.setString(2, car.getModel());
      stmt.setInt(3, car.getYear());
      stmt.setLong(4, car.getCustomerId());
      stmt.setString(5, car.getRegNo());
      return stmt.executeUpdate() > 0;
    }
  }

  @Override
  public boolean delete(String regNo) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(DELETE)) {
      stmt.setString(1, regNo);
      return stmt.executeUpdate() > 0;
    }
  }

  private Car mapRowToCar(ResultSet rs) throws SQLException {
    return new Car(
        rs.getString("regNo"),
        rs.getString("make"),
        rs.getString("model"),
        rs.getInt("year"),
        rs.getLong("customerId"));
  }

  private void setCarParameters(PreparedStatement stmt, Car car) throws SQLException {
    stmt.setString(1, car.getRegNo());
    stmt.setString(2, car.getMake());
    stmt.setString(3, car.getModel());
    stmt.setInt(4, car.getYear());
    stmt.setLong(5, car.getCustomerId());
  }
}
