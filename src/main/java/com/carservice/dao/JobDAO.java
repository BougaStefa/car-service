package com.carservice.dao;

import com.carservice.config.DatabaseConfig;
import com.carservice.model.Job;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Job entity operations with the database. Implements CRUD
 * operations for Job entities using Long as the identifier type. Provides additional functionality
 * for searching jobs by car, garage, and calculating customer costs.
 */
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
  private static final String GET_AVG_COST_BY_CUSTOMER =
      "SELECT AVG(j.cost) as avgCost "
          + "FROM Job j "
          + "INNER JOIN Car c ON j.regNo = c.regNo "
          + "WHERE c.customerId = ? "
          + "AND j.cost IS NOT NULL "
          + "AND j.dateOut IS NOT NULL";

  /**
   * Finds a job by its ID.
   *
   * @param id the job ID to search for
   * @return the Job object if found, null otherwise
   * @throws SQLException if a database access error occurs
   */
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

  /**
   * Retrieves all jobs from the database.
   *
   * @return a list of all Job objects
   * @throws SQLException if a database access error occurs
   */
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

  /**
   * Finds all jobs for a specific car by registration number.
   *
   * @param regNo the registration number of the car
   * @return a list of jobs associated with the specified car
   * @throws SQLException if a database access error occurs
   */
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

  /**
   * Finds all jobs for a specific garage.
   *
   * @param garageId the ID of the garage
   * @return a list of jobs associated with the specified garage
   * @throws SQLException if a database access error occurs
   */
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

  /**
   * Saves a new job to the database.
   *
   * @param job the Job object to save
   * @return the generated job ID, or null if the operation fails
   * @throws SQLException if a database access error occurs
   */
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

  /**
   * Updates an existing job in the database.
   *
   * @param job the Job object with updated information
   * @return true if the job was successfully updated, false otherwise
   * @throws SQLException if a database access error occurs
   */
  @Override
  public boolean update(Job job) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
      setJobParameters(stmt, job);
      stmt.setLong(6, job.getJobId());
      return stmt.executeUpdate() > 0;
    }
  }

  /**
   * Deletes a job from the database.
   *
   * @param id the ID of the job to delete
   * @return true if the job was successfully deleted, false otherwise
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
   * Calculates the average service cost for a specific customer. Only considers completed jobs with
   * non-null costs.
   *
   * @param customerId the ID of the customer
   * @return the average cost of services for the customer, or 0.0 if no completed jobs found
   * @throws SQLException if a database access error occurs
   */
  public Double getAverageServiceCostByCustomer(Long customerId) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(GET_AVG_COST_BY_CUSTOMER)) {
      stmt.setLong(1, customerId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          double avgCost = rs.getDouble("avgCost");
          return rs.wasNull() ? 0.0 : avgCost;
        }
      }
    }
    return 0.0;
  }

  /**
   * Maps a database result set row to a Job object.
   *
   * @param rs the ResultSet containing job data
   * @return a new Job object populated with the result set data
   * @throws SQLException if a database access error occurs
   */
  private Job mapRowToJob(ResultSet rs) throws SQLException {
    return new Job(
        rs.getLong("jobId"),
        rs.getLong("garageId"),
        rs.getTimestamp("dateIn").toLocalDateTime(),
        rs.getTimestamp("dateOut") != null ? rs.getTimestamp("dateOut").toLocalDateTime() : null,
        rs.getString("regNo"),
        rs.getDouble("cost"));
  }

  /**
   * Sets the common parameters of a PreparedStatement using the data from a Job object.
   *
   * @param stmt the PreparedStatement to set parameters for
   * @param job the Job object containing the data
   * @throws SQLException if a database access error occurs
   */
  private void setJobParameters(PreparedStatement stmt, Job job) throws SQLException {
    stmt.setLong(1, job.getGarageId());
    stmt.setTimestamp(2, Timestamp.valueOf(job.getDateIn()));
    stmt.setTimestamp(3, job.getDateOut() != null ? Timestamp.valueOf(job.getDateOut()) : null);
    stmt.setString(4, job.getRegNo());
    stmt.setDouble(5, job.getCost() != null ? job.getCost() : 0.0);
  }
}
