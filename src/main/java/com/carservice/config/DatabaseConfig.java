package com.carservice.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
  private static final Properties properties = new Properties();

  static {
    try {
      properties.load(
          DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties"));
    } catch (IOException e) {
      throw new RuntimeException("Could not load database properties", e);
    }
  }

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
        properties.getProperty("db.url"),
        properties.getProperty("db.username"),
        properties.getProperty("db.password"));
  }
}
