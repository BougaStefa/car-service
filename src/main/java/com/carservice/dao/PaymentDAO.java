package com.carservice.dao;

import com.carservice.config.DatabaseConfig;
import com.carservice.model.Payment;
import java.sql.*;

/**
 * Data Access Object for handling Payment entity operations with the database. Provides
 * functionality for managing payment records including creation, retrieval, and status updates.
 */
public class PaymentDAO {
  private static final String INSERT =
      "INSERT INTO Payment (jobId, amount, paymentDate, paymentMethod, paymentStatus) "
          + "VALUES (?, ?, ?, ?, ?)";
  private static final String FIND_BY_JOB = "SELECT * FROM Payment WHERE jobId = ?";
  private static final String UPDATE_STATUS =
      "UPDATE Payment SET paymentStatus = ? WHERE paymentId = ?";

  /**
   * Saves a new payment record to the database.
   *
   * @param payment the Payment object to save
   * @return the generated payment ID, or null if the operation fails
   * @throws SQLException if a database access error occurs
   */
  public Long save(Payment payment) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
      stmt.setLong(1, payment.getJobId());
      stmt.setDouble(2, payment.getAmount());
      stmt.setTimestamp(3, Timestamp.valueOf(payment.getPaymentDate()));
      stmt.setString(4, payment.getPaymentMethod());
      stmt.setString(5, payment.getPaymentStatus());

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
   * Finds a payment record associated with a specific job.
   *
   * @param jobId the ID of the job to find the payment for
   * @return the Payment object if found, null otherwise
   * @throws SQLException if a database access error occurs
   */
  public Payment findByJob(Long jobId) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_JOB)) {
      stmt.setLong(1, jobId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapRowToPayment(rs);
        }
      }
    }
    return null;
  }

  /**
   * Updates the status of an existing payment.
   *
   * @param paymentId the ID of the payment to update
   * @param status the new payment status
   * @return true if the payment status was successfully updated, false otherwise
   * @throws SQLException if a database access error occurs
   */
  public boolean updateStatus(Long paymentId, String status) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE_STATUS)) {
      stmt.setString(1, status);
      stmt.setLong(2, paymentId);
      return stmt.executeUpdate() > 0;
    }
  }

  /**
   * Maps a database result set row to a Payment object.
   *
   * @param rs the ResultSet containing payment data
   * @return a new Payment object populated with the result set data
   * @throws SQLException if a database access error occurs
   */
  private Payment mapRowToPayment(ResultSet rs) throws SQLException {
    return new Payment(
        rs.getLong("paymentId"),
        rs.getLong("jobId"),
        rs.getDouble("amount"),
        rs.getTimestamp("paymentDate").toLocalDateTime(),
        rs.getString("paymentMethod"),
        rs.getString("paymentStatus"));
  }
}
