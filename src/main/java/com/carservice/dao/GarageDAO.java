package com.carservice.dao;

import com.carservice.config.DatabaseConfig;
import com.carservice.model.Garage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Garage entity operations with the database. Implements CRUD
 * operations for Garage entities using Long as the identifier type.
 */
public class GarageDAO implements CrudDAO<Garage, Long> {
  private static final String FIND_BY_ID = "SELECT * FROM Garage WHERE garageId = ?";
  private static final String FIND_ALL = "SELECT * FROM Garage";
  private static final String INSERT =
      "INSERT INTO Garage (garageName, address, town, postCode, phoneNo) VALUES (?, ?, ?, ?, ?)";
  private static final String UPDATE =
      "UPDATE Garage SET garageName = ?, address = ?, town = ?, postCode = ?, phoneNo = ? WHERE"
          + " garageId = ?";
  private static final String DELETE = "DELETE FROM Garage WHERE garageId = ?";
  private static final String FIND_BY_NAME = "SELECT * FROM Garage WHERE LOWER(garageName) LIKE ?";

  /**
   * Finds garages by name, supporting partial and case-insensitive matches.
   *
   * @param name the name to search for (can be partial)
   * @return a list of garages whose names contain the provided value (case-insensitive)
   * @throws SQLException if a database access error occurs
   */
  public List<Garage> findByName(String name) throws SQLException {
    List<Garage> garages = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_NAME)) {
      stmt.setString(1, "%" + name.toLowerCase() + "%");
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          garages.add(mapRowToGarage(rs));
        }
      }
    }
    return garages;
  }

  /**
   * Finds a garage by its ID.
   *
   * @param id the garage ID to search for
   * @return the Garage object if found, null otherwise
   * @throws SQLException if a database access error occurs
   */
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

  /**
   * Retrieves all garages from the database.
   *
   * @return a list of all Garage objects
   * @throws SQLException if a database access error occurs
   */
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

  /**
   * Saves a new garage to the database.
   *
   * @param garage the Garage object to save
   * @return the generated garage ID, or null if the operation fails
   * @throws SQLException if a database access error occurs
   */
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

  /**
   * Updates an existing garage in the database.
   *
   * @param garage the Garage object with updated information
   * @return true if the garage was successfully updated, false otherwise
   * @throws SQLException if a database access error occurs
   */
  @Override
  public boolean update(Garage garage) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
      setGarageParameters(stmt, garage);
      stmt.setLong(6, garage.getGarageId());
      return stmt.executeUpdate() > 0;
    }
  }

  /**
   * Deletes a garage from the database.
   *
   * @param id the ID of the garage to delete
   * @return true if the garage was successfully deleted, false otherwise
   * @throws SQLException if a database access error occurs
   */
  @Override
  public boolean delete(Long id) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(DELETE)) {
      stmt.setLong(1, id);
      return stmt.executeUpdate() > 0;
    }
  }

  /**
   * Maps a database result set row to a Garage object.
   *
   * @param rs the ResultSet containing garage data
   * @return a new Garage object populated with the result set data
   * @throws SQLException if a database access error occurs
   */
  private Garage mapRowToGarage(ResultSet rs) throws SQLException {
    return new Garage(
        rs.getLong("garageId"),
        rs.getString("garageName"),
        rs.getString("address"),
        rs.getString("town"),
        rs.getString("postCode"),
        rs.getString("phoneNo"));
  }

  /**
   * Sets the common parameters of a PreparedStatement using the data from a Garage object.
   *
   * @param stmt the PreparedStatement to set parameters for
   * @param garage the Garage object containing the data
   * @throws SQLException if a database access error occurs
   */
  private void setGarageParameters(PreparedStatement stmt, Garage garage) throws SQLException {
    stmt.setString(1, garage.getGarageName());
    stmt.setString(2, garage.getAddress());
    stmt.setString(3, garage.getTown());
    stmt.setString(4, garage.getPostCode());
    stmt.setString(5, garage.getPhoneNo());
  }
}
