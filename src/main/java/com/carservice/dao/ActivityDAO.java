package com.carservice.dao;

import com.carservice.config.DatabaseConfig;
import com.carservice.model.Activity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Activity entity operations with the database. Provides methods
 * for saving and retrieving activity records.
 */
public class ActivityDAO {
  private static final String INSERT =
      "INSERT INTO Activity (type, action, description, timestamp, userId) VALUES (?, ?, ?, ?, ?)";
  private static final String FIND_RECENT =
      "SELECT * FROM Activity ORDER BY timestamp DESC LIMIT ?";

  /**
   * Saves a new activity record to the database.
   *
   * @param activity The activity object to be saved
   * @return The generated activity ID, or null if the operation fails
   * @throws SQLException if a database access error occurs
   */
  public Long save(Activity activity) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

      stmt.setString(1, activity.getType());
      stmt.setString(2, activity.getAction());
      stmt.setString(3, activity.getDescription());
      stmt.setTimestamp(4, Timestamp.valueOf(activity.getTimestamp()));
      stmt.setString(5, activity.getUserId());

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
   * Retrieves the most recent activities from the database.
   *
   * @param limit Maximum number of activities to retrieve
   * @return List of activities ordered by timestamp descending
   * @throws SQLException if a database access error occurs
   */
  public List<Activity> findRecent(int limit) throws SQLException {
    List<Activity> activities = new ArrayList<>();
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_RECENT)) {

      stmt.setInt(1, limit);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          activities.add(mapRowToActivity(rs));
        }
      }
    }
    return activities;
  }

  /**
   * Maps a database result set row to an Activity object.
   *
   * @param rs The result set containing activity data
   * @return A new Activity object populated with the result set data
   * @throws SQLException if a database access error occurs
   */
  private Activity mapRowToActivity(ResultSet rs) throws SQLException {
    return new Activity(
        rs.getLong("activityId"),
        rs.getString("type"),
        rs.getString("action"),
        rs.getString("description"),
        rs.getTimestamp("timestamp").toLocalDateTime(),
        rs.getString("userId"));
  }
}
