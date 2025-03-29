package com.carservice.dao;

import com.carservice.config.DatabaseConfig;
import com.carservice.model.Activity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityDAO {
  private static final String INSERT =
      "INSERT INTO Activity (type, action, description, timestamp, userId) VALUES (?, ?, ?, ?, ?)";
  private static final String FIND_RECENT =
      "SELECT * FROM Activity ORDER BY timestamp DESC LIMIT ?";

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
