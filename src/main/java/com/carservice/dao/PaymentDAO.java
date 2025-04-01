package com.carservice.dao;

import com.carservice.config.DatabaseConfig;
import com.carservice.model.Payment;
import java.sql.*;

public class PaymentDAO {
  private static final String INSERT =
      "INSERT INTO Payment (jobId, amount, paymentDate, paymentMethod, paymentStatus) "
          + "VALUES (?, ?, ?, ?, ?)";
  private static final String FIND_BY_JOB = "SELECT * FROM Payment WHERE jobId = ?";
  private static final String UPDATE_STATUS =
      "UPDATE Payment SET paymentStatus = ? WHERE paymentId = ?";

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

  public boolean updateStatus(Long paymentId, String status) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE_STATUS)) {
      stmt.setString(1, status);
      stmt.setLong(2, paymentId);
      return stmt.executeUpdate() > 0;
    }
  }

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
