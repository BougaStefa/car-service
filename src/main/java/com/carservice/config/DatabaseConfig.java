package com.carservice.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Configuration class for managing database connections. This class loads database properties from
 * a properties file and provides a method to establish a connection to the database.
 */
public class DatabaseConfig {
  private static final Properties properties = new Properties();

  static {
    try {
      // Load database properties from the "database.properties" file
      properties.load(
          DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties"));
    } catch (IOException e) {
      // Throw a runtime exception if the properties file cannot be loaded
      throw new RuntimeException("Could not load database properties", e);
    }
  }

  /**
   * Establishes and returns a connection to the database.
   *
   * @return a {@link Connection} object for interacting with the database
   * @throws SQLException if a database access error occurs
   */
  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
        properties.getProperty("db.url"),
        properties.getProperty("db.username"),
        properties.getProperty("db.password"));
  }
}
