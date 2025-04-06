package com.carservice.dao;

import com.carservice.config.DatabaseConfig;
import com.carservice.model.Car;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Car entity operations with the database. Implements CRUD
 * operations for Car entities using the registration number as the primary identifier.
 */
public class CarDAO implements CrudDAO<Car, String> {
  private static final String FIND_BY_ID = "SELECT * FROM Car WHERE regNo = ?";
  private static final String FIND_ALL = "SELECT * FROM Car";
  private static final String INSERT =
      "INSERT INTO Car (regNo, make, model, year, customerId) VALUES (?, ?, ?, ?, ?)";
  private static final String UPDATE =
      "UPDATE Car SET make = ?, model = ?, year = ?, customerId = ? WHERE regNo = ?";
  private static final String DELETE = "DELETE FROM Car WHERE regNo = ?";
  private static final String FIND_BY_CUSTOMER = "SELECT * FROM Car WHERE customerId = ?";

  /**
   * Finds a car by its registration number.
   *
   * @param regNo the registration number of the car to find
   * @return the Car object if found, null otherwise
   * @throws SQLException if a database access error occurs
   */
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

  /**
   * Retrieves all cars from the database.
   *
   * @return a list of all Car objects in the database
   * @throws SQLException if a database access error occurs
   */
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

  /**
   * Finds all cars associated with a specific customer.
   *
   * @param customerId the ID of the customer whose cars to find
   * @return a list of cars belonging to the specified customer
   * @throws SQLException if a database access error occurs
   */
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

  /**
   * Saves a new car to the database.
   *
   * @param car the Car object to save
   * @return the registration number of the saved car
   * @throws SQLException if a database access error occurs
   */
  @Override
  public String save(Car car) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(INSERT)) {
      setCarParameters(stmt, car);
      stmt.executeUpdate();
      return car.getRegNo();
    }
  }

  /**
   * Updates an existing car in the database.
   *
   * @param car the Car object with updated information
   * @return true if the car was successfully updated, false otherwise
   * @throws SQLException if a database access error occurs
   */
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

  /**
   * Deletes a car from the database.
   *
   * @param regNo the registration number of the car to delete
   * @return true if the car was successfully deleted, false otherwise
   * @throws SQLException if a database access error occurs
   */
  @Override
  public boolean delete(String regNo) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(DELETE)) {
      stmt.setString(1, regNo);
      return stmt.executeUpdate() > 0;
    }
  }

  /**
   * Maps a database result set row to a Car object.
   *
   * @param rs the ResultSet containing car data
   * @return a new Car object populated with the result set data
   * @throws SQLException if a database access error occurs
   */
  private Car mapRowToCar(ResultSet rs) throws SQLException {
    return new Car(
        rs.getString("regNo"),
        rs.getString("make"),
        rs.getString("model"),
        rs.getInt("year"),
        rs.getLong("customerId"));
  }

  /**
   * Sets the parameters of a PreparedStatement using the data from a Car object.
   *
   * @param stmt the PreparedStatement to set parameters for
   * @param car the Car object containing the data
   * @throws SQLException if a database access error occurs
   */
  private void setCarParameters(PreparedStatement stmt, Car car) throws SQLException {
    stmt.setString(1, car.getRegNo());
    stmt.setString(2, car.getMake());
    stmt.setString(3, car.getModel());
    stmt.setInt(4, car.getYear());
    stmt.setLong(5, car.getCustomerId());
  }
}
