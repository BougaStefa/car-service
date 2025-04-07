package com.carservice.service;

import com.carservice.dao.ActivityDAO;
import com.carservice.model.Activity;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing activities. Provides methods to log activities and retrieve recent
 * activities.
 */
public class ActivityService {
  private final ActivityDAO activityDAO;
  private static final int DEFAULT_RECENT_LIMIT = 20;

  /** Constructs an ActivityService with a default ActivityDAO instance. */
  public ActivityService() {
    this.activityDAO = new ActivityDAO();
  }

  /**
   * Logs an activity with the specified details.
   *
   * @param type the type of the activity (e.g., "INFO", "ERROR").
   * @param action the action performed (e.g., "CREATE", "DELETE").
   * @param description a description of the activity.
   * @param userId the ID of the user who performed the activity.
   */
  public void logActivity(String type, String action, String description, String userId) {
    try {
      Activity activity =
          new Activity(null, type, action, description, LocalDateTime.now(), userId);
      activityDAO.save(activity);
    } catch (SQLException e) {
      // Log error but don't throw - we don't want activity logging to break main functionality
      e.printStackTrace();
    }
  }

  /**
   * Retrieves a list of recent activities, limited to a default number.
   *
   * @return a list of recent activities.
   * @throws ServiceException if an error occurs while retrieving activities.
   */
  public List<Activity> getRecentActivity() throws ServiceException {
    try {
      return activityDAO.findRecent(DEFAULT_RECENT_LIMIT);
    } catch (SQLException e) {
      throw new ServiceException("Error retrieving recent activity", e);
    }
  }
}
