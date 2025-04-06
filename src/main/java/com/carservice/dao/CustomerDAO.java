package com.carservice.dao;

import com.carservice.config.DatabaseConfig;
import com.carservice.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Customer entity operations with the database. Implements CRUD
 * operations for Customer entities using Long as the identifier type.
 */
public class CustomerDAO implements CrudDAO<Customer, Long> {
  private static final String FIND_BY_ID = "SELECT * FROM Customer WHERE customerId = ?";
  private static final String FIND_ALL = "SELECT * FROM Customer";
  private static final String INSERT =
      "INSERT INTO Customer (forename, surname, address, postCode, phoneNo) VALUES (?, ?, ?, ?, ?)";
  private static final String UPDATE =
      "UPDATE Customer SET forename = ?, surname = ?, address = ?, postCode = ?, phoneNo = ? WHERE"
          + " customerId = ?";
  private static final String DELETE = "DELETE FROM Customer WHERE customerId = ?";
  private static final String FIND_BY_SURNAME = "SELECT * FROM Customer WHERE surname LIKE ?";

  /**
   * Finds a customer by their ID.
   *
   * @param id the customer ID to search for
   * @return the Customer object if found, null otherwise
   * @throws SQLException if a database access error occurs
   */
  @Override
  public Customer findById(Long id) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {
      stmt.setLong(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapRowToCustomer(rs);
        }
      }
    }
    return null;
  }

  /**
   * Retrieves all customers from the database.
   *
   * @return a list of all Customer objects
   * @throws SQLException if a database access error occurs
   */
  @Override
  public List<Customer> findAll() throws SQLException {
    List<Customer> customers = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(FIND_ALL)) {
      while (rs.next()) {
        customers.add(mapRowToCustomer(rs));
      }
    }
    return customers;
  }

  /**
   * Finds customers by surname, supporting partial matches.
   *
   * @param surname the surname to search for (can be partial)
   * @return a list of customers whose surnames start with the provided value
   * @throws SQLException if a database access error occurs
   */
  public List<Customer> findBySurname(String surname) throws SQLException {
    List<Customer> customers = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_SURNAME)) {
      stmt.setString(1, surname + "%");
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          customers.add(mapRowToCustomer(rs));
        }
      }
    }
    return customers;
  }

  /**
   * Saves a new customer to the database.
   *
   * @param customer the Customer object to save
   * @return the generated customer ID, or null if the operation fails
   * @throws SQLException if a database access error occurs
   */
  @Override
  public Long save(Customer customer) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
      setCustomerParameters(stmt, customer);
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
   * Updates an existing customer in the database.
   *
   * @param customer the Customer object with updated information
   * @return true if the customer was successfully updated, false otherwise
   * @throws SQLException if a database access error occurs
   */
  @Override
  public boolean update(Customer customer) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
      setCustomerParameters(stmt, customer);
      stmt.setLong(6, customer.getCustomerId());
      return stmt.executeUpdate() > 0;
    }
  }

  /**
   * Deletes a customer from the database.
   *
   * @param id the ID of the customer to delete
   * @return true if the customer was successfully deleted, false otherwise
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
   * Maps a database result set row to a Customer object.
   *
   * @param rs the ResultSet containing customer data
   * @return a new Customer object populated with the result set data
   * @throws SQLException if a database access error occurs
   */
  private Customer mapRowToCustomer(ResultSet rs) throws SQLException {
    return new Customer(
        rs.getLong("customerId"),
        rs.getString("forename"),
        rs.getString("surname"),
        rs.getString("address"),
        rs.getString("postCode"),
        rs.getString("phoneNo"));
  }

  /**
   * Sets the common parameters of a PreparedStatement using the data from a Customer object.
   *
   * @param stmt the PreparedStatement to set parameters for
   * @param customer the Customer object containing the data
   * @throws SQLException if a database access error occurs
   */
  private void setCustomerParameters(PreparedStatement stmt, Customer customer)
      throws SQLException {
    stmt.setString(1, customer.getForename());
    stmt.setString(2, customer.getSurname());
    stmt.setString(3, customer.getAddress());
    stmt.setString(4, customer.getPostCode());
    stmt.setString(5, customer.getPhoneNo());
  }
}
