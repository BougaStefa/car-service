package com.carservice.dao;

import com.carservice.config.DatabaseConfig;
import com.carservice.model.Job;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO implements CrudDAO<Job, Long> {
  private static final String FIND_BY_ID = "SELECT * FROM Job WHERE jobId = ?";
  private static final String FIND_ALL = "SELECT * FROM Job";
  private static final String INSERT =
      "INSERT INTO Job (garageId, dateIn, dateOut, regNo, cost) VALUES (?, ?, ?, ?, ?)";
  private static final String UPDATE =
      "UPDATE Job SET garageId = ?, dateIn = ?, dateOut = ?, regNo = ?, cost = ? WHERE jobId = ?";
  private static final String DELETE = "DELETE FROM Job WHERE jobId = ?";
  private static final String FIND_BY_CAR = "SELECT * FROM Job WHERE regNo = ?";
  private static final String FIND_BY_GARAGE = "SELECT * FROM Job WHERE garageId = ?";

  @Override
  public Job findById(Long id) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {
      stmt.setLong(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return mapRowToJob(rs);
        }
      }
    }
    return null;
  }

  @Override
  public List<Job> findAll() throws SQLException {
    List<Job> jobs = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(FIND_ALL)) {
      while (rs.next()) {
        jobs.add(mapRowToJob(rs));
      }
    }
    return jobs;
  }

  public List<Job> findByCar(String regNo) throws SQLException {
    List<Job> jobs = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_CAR)) {
      stmt.setString(1, regNo);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          jobs.add(mapRowToJob(rs));
        }
      }
    }
    return jobs;
  }

  public List<Job> findByGarage(Long garageId) throws SQLException {
    List<Job> jobs = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_GARAGE)) {
      stmt.setLong(1, garageId);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          jobs.add(mapRowToJob(rs));
        }
      }
    }
    return jobs;
  }

  @Override
  public Long save(Job job) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
      setJobParameters(stmt, job);
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
  public boolean update(Job job) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
      setJobParameters(stmt, job);
      stmt.setLong(6, job.getJobId());
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

  private Job mapRowToJob(ResultSet rs) throws SQLException {
    return new Job(
        rs.getLong("jobId"),
        rs.getLong("garageId"),
        rs.getTimestamp("dateIn").toLocalDateTime(),
        rs.getTimestamp("dateOut") != null ? rs.getTimestamp("dateOut").toLocalDateTime() : null,
        rs.getString("regNo"),
        rs.getDouble("cost"));
  }

  private void setJobParameters(PreparedStatement stmt, Job job) throws SQLException {
    stmt.setLong(1, job.getGarageId());
    stmt.setTimestamp(2, Timestamp.valueOf(job.getDateIn()));
    stmt.setTimestamp(3, job.getDateOut() != null ? Timestamp.valueOf(job.getDateOut()) : null);
    stmt.setString(4, job.getRegNo());
    stmt.setDouble(5, job.getCost() != null ? job.getCost() : 0.0);
  }
}
